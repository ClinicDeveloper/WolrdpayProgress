package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.worldpay.library.domain.Address;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPPostalCodeValidator;
import com.worldpay.library.webservices.network.WPHttpResponse;
import com.worldpay.library.webservices.services.customers.CreateCustomerRequest;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.tasks.CustomerCreateTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import java.util.HashMap;
import java.util.Locale;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;
import static com.worldpayment.demoapp.activities.vaultcustomers.RetrieveCustomer.responseCustomerDetails;

public class CreateCustomer extends WorldBaseActivity implements View.OnClickListener {
    Button btn_create, btn_cancel;
    WPFormEditText field_customer_id, field_first_name, field_last_name, field_phone_number, field_email_address, field_notes;
    WPFormEditText field_street_address, field_city, zip, field_company;
    WPFormEditText field_user_defined1, field_user_defined2, field_user_defined3, field_user_defined4;
    WPFormEditText spinner_state;
    private WPForm validateAlls;
    private CheckBox check_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        setActivity(CreateCustomer.this);
        mappingViews();
    }

    public void mappingViews() {

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        validateAlls = new WPForm();

        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);

        field_first_name = (WPFormEditText) findViewById(R.id.field_first_name);
        field_first_name.addValidator(new WPNotEmptyValidator("First Name is required!"));
        validateAlls.addItem(field_first_name);

        field_last_name = (WPFormEditText) findViewById(R.id.field_last_name);
        field_last_name.addValidator(new WPNotEmptyValidator("Last Name is required!"));
        validateAlls.addItem(field_last_name);

        field_phone_number = (WPFormEditText) findViewById(R.id.field_phone_number);
        field_phone_number.addValidator(new WPNotEmptyValidator("Phone Number is required!"));
//        validateAlls.addItem(field_phone_number);

        field_email_address = (WPFormEditText) findViewById(R.id.field_email_address);
        field_email_address.addValidator(new WPNotEmptyValidator("Email is required!"));
        validateAlls.addItem(field_email_address);

        check_mail = (CheckBox) findViewById(R.id.check_mail);

        field_notes = (WPFormEditText) findViewById(R.id.field_notes);
        field_notes.addValidator(new WPNotEmptyValidator("Note is required!"));
        validateAlls.addItem(field_notes);


        field_street_address = (WPFormEditText) findViewById(R.id.field_street_address);
        field_street_address.addValidator(new WPNotEmptyValidator("Line1 is required!"));
        validateAlls.addItem(field_street_address);

        field_city = (WPFormEditText) findViewById(R.id.field_city);
        field_city.addValidator(new WPNotEmptyValidator("City is required!"));
        validateAlls.addItem(field_city);

        field_company = (WPFormEditText) findViewById(R.id.field_company);
        field_company.addValidator(new WPNotEmptyValidator("Company is required!"));
//        validateAlls.addItem(field_company);


        zip = (WPFormEditText) findViewById(R.id.zip);
        zip.addValidator(new WPPostalCodeValidator("Zip Code is invalid!", Locale.US));
        validateAlls.addItem(zip);

        field_user_defined1 = (WPFormEditText) findViewById(R.id.field_user_defined1);
        field_user_defined2 = (WPFormEditText) findViewById(R.id.field_user_defined2);
        field_user_defined3 = (WPFormEditText) findViewById(R.id.field_user_defined3);
        field_user_defined4 = (WPFormEditText) findViewById(R.id.field_user_defined4);

        spinner_state = (WPFormEditText) findViewById(R.id.spinner_state);
        spinner_state.addValidator(new WPNotEmptyValidator("City is required!"));
        validateAlls.addItem(spinner_state);
//
//        spinner_state.addValidator(new WPStateCodeValidator("State is invalid!", Locale.US));
//        spinner_state.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
//                getResources().getStringArray(com.worldpay.library.R.array.states)));
//        validateAlls.addItem(spinner_state);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_create:
                KeyboardUtility.closeKeyboard(this, view);
                validationFields();
                break;

            case R.id.btn_cancel:
                KeyboardUtility.closeKeyboard(this, view);
                finish();
                break;

            default:
                break;
        }
    }


    public void validationFields() {

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();

        if (validateAlls.validateAll()) {

          //  createCustomerRequest.setCustomerID(field_customer_id.getValue());
            createCustomerRequest.setFirstName(field_first_name.getValue());
            createCustomerRequest.setLastName(field_last_name.getValue());
            createCustomerRequest.setEmail(field_email_address.getValue());
            createCustomerRequest.setPhone(field_phone_number.getValue());
            createCustomerRequest.setNotes(field_notes.getValue());

            if (check_mail.isChecked()) {
                createCustomerRequest.setSendEmailReceipts(true);
            } else {
                createCustomerRequest.setSendEmailReceipts(false);
            }
            String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
            createCustomerRequest.setAuthToken(authToken);
            createCustomerRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            createCustomerRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            createCustomerRequest.setMerchantId(MERCHANT_ID);
            createCustomerRequest.setMerchantKey(MERCHANT_KEY);

            Address address = new Address();
            address.setCountry("US");
            address.setLine1("" + field_street_address.getValue());
            address.setCity("" + field_city.getValue());
            address.setState("" + spinner_state.getValue());
            address.setZip("" + zip.getValue());
            address.setPhone("" + field_phone_number.getValue());
          //  address.setCompany("" + field_company.getValue());

            createCustomerRequest.setCompany("" + field_company.getValue());

            createCustomerRequest.setAddress(address);

            HashMap jsonObject = new HashMap();

            if (field_user_defined1.getValue() != null) {
                jsonObject.put("UDF1", field_user_defined1.getValue());
            }
            if (field_user_defined2.getValue() != null) {
                jsonObject.put("UDF2", field_user_defined2.getValue());
            }
            if (field_user_defined3.getValue() != null) {
                jsonObject.put("UDF3", field_user_defined3.getValue());
            }
            if (field_user_defined4.getValue() != null) {
                jsonObject.put("UDF4", field_user_defined4.getValue());
            }
            if (jsonObject != null) {
                createCustomerRequest.setUserDefinedFields(jsonObject);
            }
            createCustomer(createCustomerRequest);


        }
    }

    public void createCustomer(CreateCustomerRequest createCustomerRequest) {

        new CustomerCreateTask(createCustomerRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreateCustomer.this);
                startProgressBar(progressDialog, "Creating...");
            }

            @Override
            protected void onPostExecute(CustomerResponse customerResponse) {
                if (customerResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (customerResponse != null && customerResponse.getHttpStatusCode() == WPHttpResponse.HttpStatus.OK) {
                    createdDialog(customerResponse.getResult(), customerResponse, CreateCustomer.this);
                }
//                else {
//                    createdDialog(customerResponse.getResult(), customerResponse, CreateCustomer.this);
//                }

                dismissProgressBar(progressDialog);
            }
        }.execute();
    }


    public void createdDialog(String titleStr, final CustomerResponse response, final Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.master_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        TextView message = (TextView) dialogSignature.findViewById(R.id.message);

        title.setText("" + titleStr);
        message.setText("" + response.getResponseMessage());

        Button dialog_btn_negative = (Button) dialogSignature.findViewById(R.id.dialog_btn_negative);
        final Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);

        if (titleStr.equals("APPROVED")) {
            title.setTextColor(Color.parseColor("#007867"));
            dialog_btn_negative.setText("" + getResources().getString(R.string.details));
            dialog_btn_positive.setText("" + getResources().getString(R.string.done));
        } else {
            title.setTextColor(Color.parseColor("#f11e15"));
            dialog_btn_negative.setVisibility(View.GONE);
            dialog_btn_positive.setText("OK");
        }
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        responseCustomerDetails = response;

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateCustomer.this, CustomerDetailsActivity.class);
                startActivity(intent);
            }
        });

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if (dialog_btn_positive.getText().equals("OK")) {
                } else {
                    Intent navigation = new Intent(CreateCustomer.this, VaultOperations.class);
                    startActivity(navigation);
                    finish();
                }
            }
        });
    }

}
