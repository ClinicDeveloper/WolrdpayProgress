package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.worldpay.library.domain.Address;
import com.worldpay.library.domain.Customer;
import com.worldpay.library.views.iM3Form;
import com.worldpay.library.views.iM3FormEditText;
import com.worldpay.library.views.iM3NotEmptyValidator;
import com.worldpay.library.views.iM3PostalCodeValidator;
import com.worldpay.library.views.iM3SimpleFormSpinner;
import com.worldpay.library.views.iM3StateCodeValidator;
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
import static com.worldpayment.demoapp.activities.refundvoid.RefundVoidViewActivity.count;
import static com.worldpayment.demoapp.activities.vaultcustomers.RetrieveCustomer.responseCustomerDetails;

public class UpdateCustomer extends WorldBaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btn_create, btn_cancel, btn_yes, btn_no;
    iM3FormEditText field_first_name, field_last_name, field_phone_number, field_email_address, field_notes;
    iM3FormEditText field_street_address, field_city, zip, field_company;
    iM3FormEditText field_user_defined1, field_user_defined2, field_user_defined3, field_user_defined4;
    iM3SimpleFormSpinner spinner_state;
    private iM3Form validateAlls;
    String customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        setActivity(UpdateCustomer.this);
        mappingViews();

        if (getIntent().getExtras() != null) {
            customer_id = getIntent().getExtras().getString("customer_id");
            if (responseCustomerDetails != null) {
                settingFields(responseCustomerDetails);
                getSupportActionBar().setTitle("CUSTOMER ID : " + customer_id);
            } else {
                Toast.makeText(this, "Null response", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void mappingViews() {

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_create.setText("Update");
        Drawable img = getResources().getDrawable(R.mipmap.ic_update);
        img.setBounds(0, 0, 60, 60);
        btn_create.setCompoundDrawables(img, null, null, null);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_no = (Button) findViewById(R.id.btn_no);

        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);


        validateAlls = new iM3Form();

        field_first_name = (iM3FormEditText) findViewById(R.id.field_first_name);
        field_first_name.addValidator(new iM3NotEmptyValidator("First Name is required!"));
        validateAlls.addItem(field_first_name);

        field_last_name = (iM3FormEditText) findViewById(R.id.field_last_name);
        field_last_name.addValidator(new iM3NotEmptyValidator("Last Name is required!"));
        validateAlls.addItem(field_last_name);

        field_phone_number = (iM3FormEditText) findViewById(R.id.field_phone_number);
//        field_phone_number.addValidator(new iM3NotEmptyValidator("Phone Number is required!"));
//        validateAlls.addItem(field_phone_number);

        field_email_address = (iM3FormEditText) findViewById(R.id.field_email_address);
        field_email_address.addValidator(new iM3NotEmptyValidator("Email is required!"));
        validateAlls.addItem(field_email_address);

        field_notes = (iM3FormEditText) findViewById(R.id.field_notes);
//        field_notes.addValidator(new iM3NotEmptyValidator("Note is required!"));
//        validateAlls.addItem(field_notes);


        field_street_address = (iM3FormEditText) findViewById(R.id.field_street_address);
        field_street_address.addValidator(new iM3NotEmptyValidator("Line1 is required!"));
        validateAlls.addItem(field_street_address);

        field_city = (iM3FormEditText) findViewById(R.id.field_city);
        field_city.addValidator(new iM3NotEmptyValidator("City is required!"));
        validateAlls.addItem(field_city);

        field_company = (iM3FormEditText) findViewById(R.id.field_company);
        //  field_company.addValidator(new iM3NotEmptyValidator("Company is required!"));
        //  validateAlls.addItem(field_company);


        zip = (iM3FormEditText) findViewById(R.id.zip);
        zip.addValidator(new iM3PostalCodeValidator("Zip Code is invalid!", Locale.US));
        validateAlls.addItem(zip);

        field_user_defined1 = (iM3FormEditText) findViewById(R.id.field_user_defined1);
        field_user_defined2 = (iM3FormEditText) findViewById(R.id.field_user_defined2);
        field_user_defined3 = (iM3FormEditText) findViewById(R.id.field_user_defined3);
        field_user_defined4 = (iM3FormEditText) findViewById(R.id.field_user_defined4);

        spinner_state = (iM3SimpleFormSpinner) findViewById(R.id.spinner_state);
        spinner_state.addValidator(new iM3StateCodeValidator("State is invalid!", Locale.US));
        spinner_state.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(com.worldpay.library.R.array.states)));
        validateAlls.addItem(spinner_state);


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

            case R.id.btn_yes:
                count = 0;
                buttonEnabled(btn_yes, btn_no, count);
                break;

            case R.id.btn_no:
                count = 1;
                buttonEnabled(btn_no, btn_yes, count);
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

            if (count == 0) {
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
                    //    showSuccessDialog(getResources().getString(R.string.success), customerResponse.toJson(), UpdateCustomer.this);
                    responseCustomerDetails = customerResponse;
                    if (responseCustomerDetails != null) {
                        dismissProgressBar(progressDialog);
                        Intent intent = new Intent(UpdateCustomer.this, CustomerDetailsActivity.class);
                        intent.putExtra("customer_id", customer_id);
                        startActivity(intent);
                    }
                } else {
                    dismissProgressBar(progressDialog);
                    showSuccessDialog(getResources().getString(R.string.error), customerResponse.getMessage(), UpdateCustomer.this);
                }
                dismissProgressBar(progressDialog);
            }
        }.execute();
    }

    public void settingFields(CustomerResponse response) {

        //Customer OVERVIEW
        field_first_name.setText("" + response.getFirstName());
        field_last_name.setText("" + response.getLastName());
        field_email_address.setText("" + response.getEmail());
        field_company.setText("" + response.getCompany());

        if (response.getPhone() != null) {
            field_phone_number.setText("" + response.getPhone());
        }
        field_notes.setText("" + response.getNotes());

        field_street_address.setText("" + response.getAddress().getLine1());
        field_city.setText("" + response.getAddress().getCity());
        field_company.setText("" + response.getCompany());
        zip.setText("" + response.getAddress().getZip());
    }

}
