package com.worldpayment.demoapp.activities.refundvoid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.enums.ReversalType;
import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.enums.VoidType;
import com.worldpay.library.utils.WPLogger;
import com.worldpay.library.views.WPCurrencyTextWatcher;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.tasks.PaymentRefundTask;
import com.worldpay.library.webservices.tasks.PaymentVoidTask;
import com.worldpay.ui.TransactionDialogFragment;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.openApprovedDialog;

public class RefundVoidViewActivity extends WorldBaseActivity implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {

    WPFormEditText field_transaction_id, field_transaction_amount;
    private Button btn_start_transaction;
    TextView amount_textView;
    public static int count = 7;
    WPForm validateRefund, validateVoid;
    private Spinner spn_transaction_types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivity(RefundVoidViewActivity.this);
        setContentView(R.layout.activity_refund_void_view);
        initComponents();
        count = 0;
    }

    public void initComponents() {

        amount_textView = (TextView) findViewById(R.id.amount_textView);
        field_transaction_id = (WPFormEditText) findViewById(R.id.field_transaction_id);
        field_transaction_amount = (WPFormEditText) findViewById(R.id.field_transaction_amount);

        validateRefund = new WPForm();
        validateVoid = new WPForm();

        field_transaction_id.addValidator(new WPNotEmptyValidator("Transaction Id is required!"));
        validateRefund.addItem(field_transaction_id);
        validateVoid.addItem(field_transaction_id);

        field_transaction_amount
                .addValidator(new WPNotEmptyValidator("Transaction amount required!"));
        WPCurrencyTextWatcher transactionAmountTextWatcher =
                new WPCurrencyTextWatcher(field_transaction_amount, Locale.US,
                        new BigDecimal("999999.99"), true, true);
        field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validateRefund.addItem(field_transaction_amount);


        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);

        //Transaction Type Spinner
        final List<String> types = new ArrayList<String>();
        types.add("REFUND");
        types.add("VOID");
        spn_transaction_types = (Spinner) findViewById(R.id.spn_transaction_types);
        spn_transaction_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String transactionType = parent.getItemAtPosition(position).toString();
                if (transactionType.equals("REFUND")) {
                    KeyboardUtility.closeKeyboard(RefundVoidViewActivity.this, view);
                    count = 0;
                    //    field_transaction_amount.setVisibility(View.VISIBLE);
                    //    amount_textView.setVisibility(View.VISIBLE);
                } else {
                    KeyboardUtility.closeKeyboard(RefundVoidViewActivity.this, view);
                    count = 1;
//                    amount_textView.setVisibility(View.GONE);
//                    field_transaction_amount.setVisibility(View.GONE);
                }

                Log.d("transactionType", "" + transactionType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spn_transaction_types.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                types));
    }

    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {

        if (paymentResponse != null) {
            WPLogger.d(CreditDebitActivity.TAG,
                    "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                            paymentResponse);
        }
        switch (result) {
            case APPROVED:
                //   openApprovedDialog();
                break;
            case AMOUNT_REJECTED:
                break;
            case CANCELED:
                break;
            case READER_ERROR:
                break;
            case AUTHENTICATION_FAILURE:
                break;
            case DECLINED_REVERSAL_FAILED:
                break;
            case DECLINED:
                break;
            default:
                break;
        }


    }

    @Override
    public void onTransactionError(@NonNull TransactionDialogFragment.TransactionError error,
                                   @Nullable String message) {
        WPLogger.d(CreditDebitActivity.TAG, "onTransactionError :: error=" + error + ";message=" + message);
    }

    @Override
    public void onTransactionReversalFailed(ReversalRequest reversalRequest) {
        WPLogger.d(CreditDebitActivity.TAG, "onTransactionReversalFailed :: reversalType=" + reversalRequest.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_start_transaction:
                KeyboardUtility.closeKeyboard(this, v);
                showTransactionFragment();
                break;
        }
    }

    private void showTransactionFragment() {

        ReversalRequest reversalRequest = new ReversalRequest();
        TokenUtility.populateRequestHeaderFields(reversalRequest, this);

        if (count == 0) {
            if (validateRefund.validateAll()) {

                reversalRequest.setTransactionId(field_transaction_id.getValue());

                if (!TextUtils.isEmpty(field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));

                    if (!transactionAmount.toString().equals("0.00")) {
                        reversalRequest.setAmount(transactionAmount);
                        reversalRequest.setReversalType(ReversalType.REFUND);
                        refundCalling(reversalRequest);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.greaterThanZero), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        } else if (count == 1) {
            if (validateVoid.validateAll()) {
                if (field_transaction_amount.getValue() != null && !field_transaction_amount.getValue().equals("")) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));
                    if (!transactionAmount.toString().equals("0.00")) {
                        reversalRequest.setAmount(transactionAmount);
                    }
                }
                reversalRequest.setTransactionId(field_transaction_id.getValue());
                reversalRequest.setReversalType(ReversalType.VOID);
                reversalRequest.setVoidType(VoidType.VoidTypeMerchant);
                voidCalling(reversalRequest);
            }
        }
    }


    public void refundCalling(ReversalRequest reversalRequest) {

        new PaymentRefundTask(reversalRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(RefundVoidViewActivity.this);
                startProgressBar(progressDialog, "Paying refund...");
            }

            @Override
            protected void onPostExecute(PaymentResponse paymentResponse) {
                if (paymentResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (paymentResponse != null) {
                    if (paymentResponse.getResponseCode() == ResponseCode.APPROVED) {
                        openApprovedDialog("APPROVED", paymentResponse, RefundVoidViewActivity.this);
                    } else if (paymentResponse.getResponseCode() == ResponseCode.ERROR) {
                        showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                    } else if (paymentResponse.getResponseCode() == ResponseCode.DECLINED) {
                        showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + "Service Error!", RefundVoidViewActivity.this);
                }
                dismissProgressBar(progressDialog);

            }
        }.execute();
    }

    public void voidCalling(ReversalRequest reversalRequest) {

        new PaymentVoidTask(reversalRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(RefundVoidViewActivity.this);
                startProgressBar(progressDialog, "Paying void...");
            }

            @Override
            protected void onPostExecute(PaymentResponse paymentResponse) {
                if (paymentResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }
                if (paymentResponse != null) {
                    if (paymentResponse.getResponseCode() == ResponseCode.APPROVED) {
                        openApprovedDialog("APPROVED", paymentResponse, RefundVoidViewActivity.this);
                    } else if (paymentResponse.getResponseCode() == ResponseCode.ERROR) {
                        showDialogView(getResources().getString(R.string.error), "" + paymentResponse.getResponseMessage(), RefundVoidViewActivity.this);
                    }
                } else {
                    showDialogView(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + " Service Error!", RefundVoidViewActivity.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}