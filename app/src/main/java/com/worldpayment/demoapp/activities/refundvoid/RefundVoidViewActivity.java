package com.worldpayment.demoapp.activities.refundvoid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.worldpay.library.enums.ReversalType;
import com.worldpay.library.enums.TransactionResult;
import com.worldpay.library.enums.VoidType;
import com.worldpay.library.utils.iM3Logger;
import com.worldpay.library.views.iM3CurrencyTextWatcher;
import com.worldpay.library.views.iM3Form;
import com.worldpay.library.views.iM3FormEditText;
import com.worldpay.library.views.iM3NotEmptyValidator;
import com.worldpay.library.webservices.network.iM3HttpResponse;
import com.worldpay.library.webservices.services.payments.PaymentResponse;
import com.worldpay.library.webservices.services.payments.ReversalRequest;
import com.worldpay.library.webservices.tasks.PaymentRefundTask;
import com.worldpay.library.webservices.tasks.PaymentVoidTask;
import com.worldpay.ui.TransactionDialogFragment;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import java.math.BigDecimal;
import java.util.Locale;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.openApprovedDialog;

public class RefundVoidViewActivity extends WorldBaseActivity implements View.OnClickListener, TransactionDialogFragment.TransactionDialogFragmentListener {

    iM3FormEditText field_transaction_id, field_transaction_amount;
    private Button btn_refund, btn_void, btn_start_transaction;
    TextView amount_textView;
    public static int count = 7;
    iM3Form validateRefund, validateVoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivity(RefundVoidViewActivity.this);
        setContentView(R.layout.activity_refund_void_view);
        initComponents();
        count = 0;
    }

    public void initComponents() {

        btn_refund = (Button) findViewById(R.id.btn_refund);
        btn_refund.setOnClickListener(this);

        btn_void = (Button) findViewById(R.id.btn_void);
        btn_void.setOnClickListener(this);

        amount_textView = (TextView) findViewById(R.id.amount_textView);
        field_transaction_id = (iM3FormEditText) findViewById(R.id.field_transaction_id);
        field_transaction_amount = (iM3FormEditText) findViewById(R.id.field_transaction_amount);

        validateRefund = new iM3Form();
        validateVoid = new iM3Form();

        field_transaction_id.addValidator(new iM3NotEmptyValidator("Transaction ID is required!"));
        validateRefund.addItem(field_transaction_id);
        validateVoid.addItem(field_transaction_id);

        field_transaction_amount
                .addValidator(new iM3NotEmptyValidator("Transaction amount required!"));
        iM3CurrencyTextWatcher transactionAmountTextWatcher =
                new iM3CurrencyTextWatcher(field_transaction_amount, Locale.US,
                        new BigDecimal("999999.99"), true, true);
        field_transaction_amount.addTextChangedListener(transactionAmountTextWatcher);
        validateRefund.addItem(field_transaction_amount);


        btn_start_transaction = (Button) findViewById(R.id.btn_start_transaction);
        btn_start_transaction.setOnClickListener(this);
    }

    @Override
    public void onTransactionComplete(TransactionResult result, PaymentResponse paymentResponse) {
        iM3Logger.d(CreditDebitActivity.TAG,
                "onTransactionComplete :: result=" + result + ";paymentResponse=" +
                        paymentResponse);

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
        iM3Logger.d(CreditDebitActivity.TAG, "onTransactionError :: error=" + error + ";message=" + message);
    }

    @Override
    public void onTransactionReversalFailed(ReversalRequest reversalRequest) {
        iM3Logger.d(CreditDebitActivity.TAG, "onTransactionReversalFailed :: reversalType=" + reversalRequest.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_refund:
                KeyboardUtility.closeKeyboard(this, v);
                count = 0;
                field_transaction_amount.setVisibility(View.VISIBLE);
                amount_textView.setVisibility(View.VISIBLE);
                buttonEnabled(btn_refund, btn_void, count);
                break;

            case R.id.btn_void:
                KeyboardUtility.closeKeyboard(this, v);
                count = 1;
                amount_textView.setVisibility(View.GONE);
                field_transaction_amount.setVisibility(View.GONE);
                buttonEnabled(btn_void, btn_refund, count);
                break;

            case R.id.btn_start_transaction:
                KeyboardUtility.closeKeyboard(this, v);
                showTransactionFragment();
                break;
        }
    }

    private void showTransactionFragment() {

        ReversalRequest reversalRequest = new ReversalRequest();
        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        reversalRequest.setAuthToken(authToken);
        reversalRequest.setMerchantId(MERCHANT_ID);
        reversalRequest.setMerchantKey(MERCHANT_KEY);
        reversalRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
        reversalRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);

        if (count == 0) {
            if (validateRefund.validateAll()) {

                reversalRequest.setTransactionId(field_transaction_id.getValue());

                if (!TextUtils.isEmpty(field_transaction_amount.getValue())) {
                    BigDecimal transactionAmount = new BigDecimal(field_transaction_amount.getValue().replaceAll("[^\\d.]", ""));

                    if (!transactionAmount.toString().equals("0.00")) {
                        reversalRequest.setAmount(transactionAmount);
                        reversalRequest.setReversalType(ReversalType.REFUND);
                        //  reversalRequest.setVoidType(VoidType.VoidTypeMerchant);
                        refundCalling(reversalRequest);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.greaterThanZero), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        } else if (count == 1) {
            if (validateVoid.validateAll()) {
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

                Log.d("RFUND RESPONSE : ", "" + paymentResponse.toJson());
                if (paymentResponse != null && paymentResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {

                    Log.d("RFUND RESPONSE : ", "" + paymentResponse.getTransactionResponse().getResponseText());
                    if (!paymentResponse.getTransactionResponse().getAmount().toString().trim().equals("0.0".trim())) {
                        openApprovedDialog(getResources().getString(R.string.approvedNoVault), paymentResponse.getTransactionResponse(), RefundVoidViewActivity.this);
                    } else
                        showSuccessDialog(getResources().getString(R.string.error), "" + paymentResponse.getTransactionResponse().getResponseText(), RefundVoidViewActivity.this);
                } else {
                    showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + paymentResponse.getMessage(), RefundVoidViewActivity.this);
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
                Log.d("reversalRequest", "" + paymentResponse.toJson());
                if (paymentResponse != null && paymentResponse.getHttpStatusCode() == iM3HttpResponse.iM3HttpStatus.OK) {
                    if (!paymentResponse.getTransactionResponse().getAmount().toString().trim().equals("0.0".trim())) {
                        openApprovedDialog(getResources().getString(R.string.approvedNoVault), paymentResponse.getTransactionResponse(), RefundVoidViewActivity.this);
                    } else
                        showSuccessDialog(getResources().getString(R.string.error), "" + paymentResponse.getTransactionResponse().getResponseText(), RefundVoidViewActivity.this);

                } else {
                    showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + paymentResponse.getMessage(), RefundVoidViewActivity.this);
                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }
}