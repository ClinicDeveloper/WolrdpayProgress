package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.customers.GetCustomerRequest;
import com.worldpay.library.webservices.tasks.CustomerGetTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class RetrieveCustomer extends WorldBaseActivity implements View.OnClickListener {

    Button btn_search, btn_cancel;
    private WPFormEditText field_customer_id;
    WPForm validateID;
    RecyclerView recyclerView;
    public static CustomerResponse responseCustomerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_customer);
        setActivity(RetrieveCustomer.this);
        mappingViews();
    }

    public void mappingViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_search.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        validateID = new WPForm();
        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        field_customer_id.addValidator(new WPNotEmptyValidator("Customer Id is required!"));
        validateID.addItem(field_customer_id);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_search:
                KeyboardUtility.closeKeyboard(this, v);
                fetchCustomers();
                break;

            case R.id.btn_cancel:
                finish();
                break;

            default:
                break;
        }
    }


    public void fetchCustomers() {

        GetCustomerRequest getCustomerRequest = new GetCustomerRequest();
        if (validateID.validateAll()) {
            getCustomerRequest.setId(field_customer_id.getValue());
            String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
            getCustomerRequest.setAuthToken(authToken);
            getCustomerRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            getCustomerRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            getCustomerRequest.setMerchantId(MERCHANT_ID);
            getCustomerRequest.setMerchantKey(MERCHANT_KEY);

            new CustomerGetTask(getCustomerRequest) {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(RetrieveCustomer.this);
                    startProgressBar(progressDialog, "Retrieving customers...");
                }

                @Override
                protected void onPostExecute(CustomerResponse customerResponse) {

                    if (customerResponse != null) {
                        Log.d("customerResponse", "" + customerResponse.toJson());
                        if (customerResponse.hasError()) {
                            dismissProgressBar(progressDialog);
                            showDialog(getResources().getString(R.string.error), customerResponse.getMessage(), RetrieveCustomer.this);
                        }
                        if (customerResponse.getResponseCode() == ResponseCode.APPROVED) {
                            responseCustomerDetails = customerResponse;
                            dismissProgressBar(progressDialog);
                            Intent retrieve = new Intent(RetrieveCustomer.this, CustomerDetailsActivity.class);
                            retrieve.putExtra("customer_id", field_customer_id.getValue());
                            startActivity(retrieve);
                        } else {
                            dismissProgressBar(progressDialog);
                            showDialog(getResources().getString(R.string.error), customerResponse.getResponseMessage(), RetrieveCustomer.this);
                        }
                    }
                    dismissProgressBar(progressDialog);
                }
            }.execute();

        }
    }

}
