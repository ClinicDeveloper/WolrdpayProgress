package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldpay.library.domain.Address;
import com.worldpay.library.views.WPForm;
import com.worldpay.library.views.WPFormEditText;
import com.worldpay.library.views.WPNotEmptyValidator;
import com.worldpay.library.webservices.services.customers.CreateCustomerRequest;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.tasks.CustomerCreateTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.KeyboardUtility;
import com.worldpayment.demoapp.utility.TokenUtility;

import org.json.JSONObject;

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
//        validateAlls.addItem(field_first_name);

        field_last_name = (WPFormEditText) findViewById(R.id.field_last_name);
        field_last_name.addValidator(new WPNotEmptyValidator("Last Name is required!"));
//        validateAlls.addItem(field_last_name);

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
        //   validateAlls.addItem(field_company);


        zip = (WPFormEditText) findViewById(R.id.zip);
        zip.addValidator(new WPNotEmptyValidator("Zip Code is invalid!"));
        validateAlls.addItem(zip);

        field_user_defined1 = (WPFormEditText) findViewById(R.id.field_user_defined1);
        field_user_defined2 = (WPFormEditText) findViewById(R.id.field_user_defined2);
        field_user_defined3 = (WPFormEditText) findViewById(R.id.field_user_defined3);
        field_user_defined4 = (WPFormEditText) findViewById(R.id.field_user_defined4);

        spinner_state = (WPFormEditText) findViewById(R.id.spinner_state);
        spinner_state.addValidator(new WPNotEmptyValidator("State is required!"));
        validateAlls.addItem(spinner_state);
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

            createCustomerRequest.setId("" + field_customer_id.getValue());
            createCustomerRequest.setFirstName("" + field_first_name.getValue());
            createCustomerRequest.setLastName("" + field_last_name.getValue());
            createCustomerRequest.setEmail("" + field_email_address.getValue());
            createCustomerRequest.setPhone("" + field_phone_number.getValue());
            createCustomerRequest.setNotes("" + field_notes.getValue());
            createCustomerRequest.setCompany("" + field_company.getValue());

            if (check_mail.isChecked()) {
                createCustomerRequest.setSendEmailReceipts(true);
            } else {
                createCustomerRequest.setSendEmailReceipts(false);
            }
            TokenUtility.populateRequestHeaderFields(createCustomerRequest, this);

            Address address = new Address();
            address.setCountry("US");
            address.setLine1("" + field_street_address.getValue());
            address.setCity("" + field_city.getValue());
            address.setState("" + spinner_state.getValue());
            address.setZip("" + zip.getValue());
            address.setPhone("" + field_phone_number.getValue());
            //  address.setCompany("" + field_company.getValue());
            createCustomerRequest.setAddress(address);

            try {
                JSONObject jsonObject = new JSONObject();

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

                createCustomerRequest.setUserDefinedFields(jsonObject);
            } catch (Exception e) {
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

                if (customerResponse != null) {
                    createdDialog(customerResponse.getResult(), customerResponse, CreateCustomer.this);
                } else {
                    createdDialog("ERROR", customerResponse, CreateCustomer.this);
                }

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

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent retrieve = new Intent(CreateCustomer.this, CustomerDetailsActivity.class);
                Gson gson = new Gson();
                String responseStr = gson.toJson(response);
                retrieve.putExtra("response", responseStr);
                startActivity(retrieve);

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
