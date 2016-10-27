package com.worldpayment.demoapp.activities.debitcredit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.worldpay.library.webservices.network.iM3HttpResponse;
import com.worldpay.library.webservices.services.batches.BatchResponse;
import com.worldpay.library.webservices.services.batches.GetCurrentBatchRequest;
import com.worldpay.library.webservices.services.payments.TransactionResponse;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchRequest;
import com.worldpay.library.webservices.services.transactions.GetTransactionsBatchResponse;
import com.worldpay.library.webservices.tasks.BatchGetCurrentTask;
import com.worldpay.library.webservices.tasks.TransactionGetBatchTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.adapters.SettlementAdapter;

import java.util.List;

import static com.worldpayment.demoapp.WorldBaseActivity.dismissProgressBar;
import static com.worldpayment.demoapp.WorldBaseActivity.showSuccessDialog;
import static com.worldpayment.demoapp.WorldBaseActivity.startProgressBar;
import static com.worldpayment.demoapp.activities.debitcredit.DebitCreditActivity.PREF_AUTH_TOKEN;

public class TransactionListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionResponse[] transactionResponses;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_search);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Batch Details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        if (getIntent().getExtras() != null) {

            GetTransactionsBatchRequest getTransactionsBatchRequest = new GetTransactionsBatchRequest();
            String batchId = getIntent().getExtras().getString("batchId");
            getSupportActionBar().setTitle("Batch ID : " + batchId);
            getTransactionsBatchRequest.setBatchId(batchId);
            getTransactionsBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getTransactionsBatchRequest.setMerchantId(BuildConfig.MERCHANT_ID);
            getTransactionsBatchRequest.setMerchantKey(BuildConfig.MERCHANT_KEY);
            getTransactionsBatchRequest.setAuthToken(authToken);
            getTransactionsBatchRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            getBatchInformation(getTransactionsBatchRequest);

        } else {
            GetCurrentBatchRequest getCurrentBatchRequest = new GetCurrentBatchRequest();
            getCurrentBatchRequest.setAuthToken(authToken);
            getCurrentBatchRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getCurrentBatchRequest.setMerchantKey(BuildConfig.MERCHANT_KEY);
            getCurrentBatchRequest.setMerchantId(BuildConfig.MERCHANT_ID);
            getCurrentBatchRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            getCurrentBatchInformation(getCurrentBatchRequest);
        }
    }


    //Current Batch Info
    public void getCurrentBatchInformation(GetCurrentBatchRequest getCurrentBatchRequest) {

        new BatchGetCurrentTask(getCurrentBatchRequest) {
            ProgressDialog progressDialog;
            List<TransactionResponse> transactionResponses;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(TransactionListActivity.this);
                startProgressBar(progressDialog, "Getting Current Batch...");
            }

            @Override
            protected void onPostExecute(BatchResponse batchResponse) {
                if (batchResponse.hasError()) {
                    return;
                }
                if (batchResponse != null && batchResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                    getSupportActionBar().setTitle("Batch ID : " + batchResponse.getId());
                    transactionResponses = batchResponse.getTransactions();
                    if (transactionResponses != null) {
                        SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, transactionResponses);
                        adapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TransactionListActivity.this, 1);

                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);

                    } else {
                        showSuccessDialog(getResources().getString(R.string.success), getResources().getString(R.string.noTransaction), TransactionListActivity.this);
                    }
                } else {
                    showSuccessDialog(getResources().getString(R.string.error), batchResponse.getMessage(), TransactionListActivity.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();

    }

// Info With Batch Id

    public void getBatchInformation(GetTransactionsBatchRequest getCurrentBatchRequest) {

        new TransactionGetBatchTask(getCurrentBatchRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(TransactionListActivity.this);
                startProgressBar(progressDialog, "Getting data...");
            }

            @Override
            protected void onPostExecute(GetTransactionsBatchResponse getTransactionsBatchResponse) {
                if (getTransactionsBatchResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }
                Log.d("batchResponse", "" + getTransactionsBatchResponse.getTransactions());
//                if (getTransactionsBatchResponse.getTransactions() != null) {
//                    ArrayList<BatchResponse> list = new ArrayList<BatchResponse>();
//
//                    try {
//                        if (transactionResponses != null) {
//
//                            if (transactionResponses.length == 1) {
//
//                            }
//                            for (int i = 0; i < transactionResponses.length; i++) {
//                                JSONObject jsonObject = new JSONObject(transactionResponses[i].toString());
//                                String Transaction = jsonObject.getString("Transaction");
//                                Gson gson = new Gson();
//                                gson.fromJson(Transaction, TransactionResponse.class);
//
//                                BatchResponse gotFromJson = gson.fromJson(Transaction, BatchResponse.class);
//                                list.add(gotFromJson);
//                                getSupportActionBar().setTitle("Batch ID : " + gotFromJson.getId());
//
//
//                            }
//                        } else {
//
//                            noTransaction(TransactionListActivity.this);
//                            //     Toast.makeText(TransactionListActivity.this, "Null response", Toast.LENGTH_SHORT).show();
//
//
////                            //Manual Data Call
////                            String json = loadJSONFromAsset();
////                            JSONObject jsonObject = new JSONObject(json);
////                            String Transaction = jsonObject.getString("Transaction");
////                            Gson gson = new Gson();
////
////                            gson.fromJson(Transaction, TransactionResponse.class);
////                            TransactionResponse gotFromJson = gson.fromJson(Transaction, TransactionResponse.class);
////                            list.add(gotFromJson);
////                            list.add(gotFromJson);
////                            list.add(gotFromJson);
//                        }
//
////                        SettlementAdapter adapter = new SettlementAdapter(TransactionListActivity.this, list);
////                        adapter.notifyDataSetChanged();
////                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TransactionListActivity.this, 1);
////
////                        recyclerView.setLayoutManager(mLayoutManager);
////                        recyclerView.setHasFixedSize(true);
////                        recyclerView.setItemAnimator(new DefaultItemAnimator());
////                        recyclerView.setAdapter(adapter);
//
//                    } catch (JSONException ed) {
//                    }
//                } else {
                //              finish();
                //        }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}
