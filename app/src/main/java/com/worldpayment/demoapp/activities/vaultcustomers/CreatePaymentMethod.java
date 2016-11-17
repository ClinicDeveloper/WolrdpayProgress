package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.worldpay.library.domain.Card;
import com.worldpay.library.domain.PaymentMethod;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.webservices.services.paymentmethods.CreatePaymentMethodRequest;
import com.worldpay.library.webservices.services.paymentmethods.PaymentMethodResponse;
import com.worldpay.library.webservices.tasks.PaymentMethodCreateTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class CreatePaymentMethod extends WorldBaseActivity implements View.OnClickListener {

    private Button btn_create, btn_cancel;
    private WPFormEditText customer_id, payment_id;
    private RadioGroup radioPaymentType;
    private RadioButton radioButton;
    private LinearLayout card_layout, check_layout;
//    //Card
//    private
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_payment_account);
        setActivity(CreatePaymentMethod.this);
        mappingViews();
    }

    public void mappingViews() {

        radioPaymentType = (RadioGroup) findViewById(R.id.radioPaymentType);
        radioPaymentType.setOnClickListener(this);

        card_layout = (LinearLayout) findViewById(R.id.card_layout);
        check_layout = (LinearLayout) findViewById(R.id.check_layout);

        radioPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                if (null != radioButton && checkedId > -1) {
                    if (radioButton.getText().equals("Card")) {
                        card_layout.setVisibility(View.VISIBLE);
                        check_layout.setVisibility(View.GONE);

                    } else if (radioButton.getText().equals("Check")) {
                        check_layout.setVisibility(View.VISIBLE);
                        card_layout.setVisibility(View.GONE);

                    }
                }
            }
        });
        btn_create = (Button) findViewById(R.id.btn_create);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_create:
                Toast.makeText(this, "SDK in progress", Toast.LENGTH_SHORT).show();
//                setFields();
                break;

            case R.id.btn_cancel:
                finish();

            default:
                break;
        }
    }

    public void setFields() {

        CreatePaymentMethodRequest createPaymentMethodRequest = new CreatePaymentMethodRequest();

        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
        createPaymentMethodRequest.setAuthToken(authToken);
        createPaymentMethodRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
        createPaymentMethodRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
        createPaymentMethodRequest.setMerchantId(MERCHANT_ID);
        createPaymentMethodRequest.setMerchantKey(MERCHANT_KEY);

        createPaymentMethodRequest.setCustomerId("5000037");
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setNotes("Hey It is test");
        Card card = new Card();
        card.setNumber("4111111111111111");
        card.setCvv("123");
        card.setExpirationMonth(8);
        card.setExpirationYear(2018);
        createPaymentMethodRequest.setCard(card);
        creatingPaymentMethod(createPaymentMethodRequest);

    }

    public void creatingPaymentMethod(CreatePaymentMethodRequest createPaymentMethodRequest) {

        new PaymentMethodCreateTask(createPaymentMethodRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreatePaymentMethod.this);
                startProgressBar(progressDialog, "Paying refund...");
            }

            @Override
            protected void onPostExecute(PaymentMethodResponse paymentMethodResponse) {
                if (paymentMethodResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (paymentMethodResponse != null) {
                    Log.d("RFUND RESPONSE : ", "" + paymentMethodResponse.toJson());
                }
//                if (paymentMethodResponse != null && paymentMethodResponse.getHttpStatusCode() == WPHttpResponse.HttpStatus.OK) {
//
//                    if (!paymentMethodResponse.getTransactionResponse().getAmount().toString().trim().equals("0.0".trim())) {
//                        openApprovedDialog("APPROVED", paymentMethodResponse.getTransactionResponse(), RefundVoidViewActivity.this);
//                    } else
//                        showSuccessDialog(getResources().getString(R.string.error), "" + paymentMethodResponse.getTransactionResponse().getResponseText(), RefundVoidViewActivity.this);
//                } else {
//                    showSuccessDialog(getResources().getString(R.string.error), getResources().getString(R.string.transactionFailed) + "\n" + paymentResponse.getMessage(), RefundVoidViewActivity.this);
//                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }

}
