package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.worldpay.library.domain.Address;
import com.worldpay.library.domain.Customer;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.views.WPPostalCodeValidator;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.customers.UpdateCustomerRequest;
import com.worldpay.library.webservices.tasks.CustomerUpdateTask;
import com.worldpayment.demoapp.BuildConfig;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import java.util.Locale;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;
import static com.worldpayment.demoapp.activities.vaultcustomers.RetrieveCustomer.responseCustomerDetails;

public class UpdateCustomer extends WorldBaseActivity implements View.OnClickListener {

    private Button btn_create, btn_cancel;
    WPFormEditText field_customer_id, field_first_name, field_last_name, field_phone_number, field_email_address, field_notes;
    WPFormEditText field_street_address, field_city, zip, field_company;
    WPFormEditText field_user_defined1, field_user_defined2, field_user_defined3, field_user_defined4;
    WPFormEditText spinner_state;
    private WPForm validateAlls;
    String customer_id;
    private CheckBox check_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        setActivity(UpdateCustomer.this);
        mappingViews();

        if (responseCustomerDetails != null) {
            settingFields(responseCustomerDetails);
        }
    }

    public void mappingViews() {


        btn_create = (Button) findViewById(R.id.btn_create);
        btn_create.setText("Update");
        Drawable img = getResources().getDrawable(R.mipmap.ic_update);
        img.setBounds(0, 0, 60, 60);
        btn_create.setCompoundDrawables(img, null, null, null);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        check_mail = (CheckBox) findViewById(R.id.check_mail);
        validateAlls = new WPForm();

        field_customer_id = (WPFormEditText) findViewById(R.id.field_customer_id);
        field_first_name = (WPFormEditText) findViewById(R.id.field_first_name);
        field_first_name.addValidator(new WPNotEmptyValidator("First Name is required!"));
        validateAlls.addItem(field_first_name);

        field_last_name = (WPFormEditText) findViewById(R.id.field_last_name);
        field_last_name.addValidator(new WPNotEmptyValidator("Last Name is required!"));
        validateAlls.addItem(field_last_name);

        field_phone_number = (WPFormEditText) findViewById(R.id.field_phone_number);

        field_email_address = (WPFormEditText) findViewById(R.id.field_email_address);
        field_email_address.addValidator(new WPNotEmptyValidator("Email is required!"));
        validateAlls.addItem(field_email_address);

        field_notes = (WPFormEditText) findViewById(R.id.field_notes);

        field_street_address = (WPFormEditText) findViewById(R.id.field_street_address);
        field_street_address.addValidator(new WPNotEmptyValidator("Line1 is required!"));
        validateAlls.addItem(field_street_address);

        field_city = (WPFormEditText) findViewById(R.id.field_city);
        field_city.addValidator(new WPNotEmptyValidator("City is required!"));
        validateAlls.addItem(field_city);

        field_company = (WPFormEditText) findViewById(R.id.field_company);

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

//        spinner_state = (WPFormEditText) findViewById(R.id.spinner_state);
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
                // Toast.makeText(this, "SDK implementation in process", Toast.LENGTH_SHORT).show();
                validationFields();
                break;

            case R.id.btn_cancel:
                break;

            default:
                break;
        }
    }


    public void validationFields() {

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        updateCustomerRequest.setId(customer_id);

        if (validateAlls.validateAll()) {

            Customer customer = new Customer();

            customer.setFirstName("" + field_first_name.getValue());
            customer.setLastName("" + field_last_name.getValue());
            customer.setEmail("" + field_email_address.getValue());
            customer.setPhone("" + field_phone_number.getValue());
            customer.setNotes("" + field_notes.getValue());

            if (check_mail.isChecked()) {
                customer.setSendEmailReceipts(true);
            } else {
                customer.setSendEmailReceipts(false);
            }

            String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);
            updateCustomerRequest.setAuthToken(authToken);
            updateCustomerRequest.setDeveloperId(BuildConfig.DEVELOPER_ID);
            updateCustomerRequest.setApplicationVersion(BuildConfig.VERSION_NAME);
            updateCustomerRequest.setMerchantId(MERCHANT_ID);
            updateCustomerRequest.setMerchantKey(MERCHANT_KEY);

            Address address = new Address();
            address.setCountry("US");
            address.setLine1("" + field_street_address.getValue());
            address.setCity("" + field_city.getValue());
            address.setState("" + spinner_state.getValue());
            address.setZip("" + zip.getValue());

            customer.setAddress(address);
            updateCustomerRequest.setCustomer(customer);

//            createCustomerRequest.setUserDefinedFields();
            updateCustomer(updateCustomerRequest);

        }
    }

    public void updateCustomer(UpdateCustomerRequest createCustomerRequest) {

        new CustomerUpdateTask(createCustomerRequest) {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(UpdateCustomer.this);
                startProgressBar(progressDialog, "Updating...");
            }

            @Override
            protected void onPostExecute(CustomerResponse customerResponse) {

                Log.d("customerResponse", "" + customerResponse.toJson());

                if (customerResponse.hasError()) {
                    dismissProgressBar(progressDialog);
                    return;
                }

                if (customerResponse != null) {
                    dismissProgressBar(progressDialog);
                    //    showDialog(getResources().getString(R.string.success), customerResponse.toJson(), UpdateCustomer.this);
                    responseCustomerDetails = customerResponse;
                    if (responseCustomerDetails != null) {
                        dismissProgressBar(progressDialog);
                        Intent intent = new Intent(UpdateCustomer.this, CustomerDetailsActivity.class);
                        intent.putExtra("customer_id", customer_id);
                        startActivity(intent);
                    }
                } else {
                    dismissProgressBar(progressDialog);
                    showDialog(getResources().getString(R.string.error), customerResponse.getMessage(), UpdateCustomer.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }

    public void settingFields(CustomerResponse response) {

        //Customer OVERVIEW
        // field_customer_id.setText("" + response.getCustomerId());
        field_first_name.setText("" + response.getFirstName());
        field_last_name.setText("" + response.getLastName());
        field_email_address.setText("" + response.getEmail());
        field_company.setText("" + response.getCompany());

        if (response.getPhone() != null) {
            field_phone_number.setText("" + response.getPhone());
        }
        field_notes.setText("" + response.getNotes());

        if (response.getAddress() != null) {
            if (response.getAddress().getLine1() != null) {
                field_street_address.setText("" + response.getAddress().getLine1());
            }
            if (response.getAddress().getCity() != null) {
                field_city.setText("" + response.getAddress().getCity());
            }
//            if (response.getAddress().getCompany() != null) {
//                field_company.setText("" + response.getCompany());
//            }
            if (response.getAddress().getZip() != null) {
                zip.setText("" + response.getAddress().getZip());
            }
        }
        if (response.isSendEmailReceipts()) {
            check_mail.setChecked(true);
        } else {
            check_mail.setChecked(false);
        }
    }

}
