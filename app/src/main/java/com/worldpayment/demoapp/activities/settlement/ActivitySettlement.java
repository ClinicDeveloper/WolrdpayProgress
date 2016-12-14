package com.worldpayment.demoapp.activities.settlement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.CloseCurrentBatchRequest;
import com.worldpay.library.webservices.tasks.BatchCloseCurrentTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

public class ActivitySettlement extends WorldBaseActivity implements View.OnClickListener {

    EditText field_transaction_id;
    Button btn_get_batch, btn_close_current_batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        setActivity(ActivitySettlement.this);
        initComponents();
    }

    public void initComponents() {

        field_transaction_id = (EditText) findViewById(R.id.field_transaction_id);
        btn_get_batch = (Button) findViewById(R.id.btn_get_batch);
        btn_close_current_batch = (Button) findViewById(R.id.btn_close_current_batch);

        btn_get_batch.setOnClickListener(this);
        btn_close_current_batch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_get_batch:
                if (!field_transaction_id.getText().equals(" ") && field_transaction_id.getText().length() > 5) {

                    KeyboardUtility.closeKeyboard(this, view);
                    Intent intent = new Intent(ActivitySettlement.this, TransactionListActivity.class);
                    intent.putExtra("batchId", field_transaction_id.getText().toString());
                    startActivity(intent);

                } else {
                    KeyboardUtility.closeKeyboard(this, view);
                    Intent intent = new Intent(ActivitySettlement.this, TransactionListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_close_current_batch:

                KeyboardUtility.closeKeyboard(this, view);
                CloseCurrentBatchRequest closeCurrentBatchRequest = new CloseCurrentBatchRequest();
                TokenUtility.populateRequestHeaderFields(closeCurrentBatchRequest, this);
                closeCurrentBatch(closeCurrentBatchRequest);
                break;

            default:
                break;
        }
    }

    public void closeCurrentBatch(CloseCurrentBatchRequest closeCurrentBatchRequest) {

        new BatchCloseCurrentTask(closeCurrentBatchRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ActivitySettlement.this);
                startProgressBar(progressDialog, "Closing Current Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {
                if (batchResponse != null) {
                    if (batchResponse.getResponseCode() == ResponseCode.APPROVED) {
                        showDialogView(getResources().getString(R.string.success), "Batch " + batchResponse.getId() + " " + getResources().getString(R.string.batchClosed), ActivitySettlement.this);
                    } else {
                        showDialogView(getResources().getString(R.string.error), batchResponse.getResponseMessage(), ActivitySettlement.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), "Null response! Service Error!", ActivitySettlement.this);
                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
