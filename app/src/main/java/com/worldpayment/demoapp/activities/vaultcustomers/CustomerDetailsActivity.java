package com.worldpayment.demoapp.activities.vaultcustomers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpayment.demoapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.worldpayment.demoapp.activities.vaultcustomers.RetrieveCustomer.responseCustomerDetails;

public class CustomerDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //CUSTOMER INFO
    TextView tv_customer_id, tv_first_name, tv_last_name, tv_email, tv_send_email_address, tv_notes;
    //ADDRESS
    TextView tv_line_one, tv_city, tv_state, tv_zip_code, tv_country, tv_company, tv_phone;
    //USER DEFINED FIELDS
    TextView tv_udfname;

    Button btn_cancel, btn_edit, btn_delete, btn_payment_method;
    TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        initComponents();

        settingFields(responseCustomerDetails);
    }

    public void initComponents() {

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
        toolbar_title = (TextView) appBarLayout.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //CUSTOMER INFO
        tv_customer_id = (TextView) findViewById(R.id.tv_customer_id);
        tv_first_name = (TextView) findViewById(R.id.tv_first_name);
        tv_last_name = (TextView) findViewById(R.id.tv_last_name);
        tv_email = (TextView) findViewById(R.id.tv_email_address);
        tv_send_email_address = (TextView) findViewById(R.id.tv_send_email_address);
        tv_notes = (TextView) findViewById(R.id.tv_notes);

        //ADDRESS
        tv_line_one = (TextView) findViewById(R.id.tv_line_one);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_zip_code = (TextView) findViewById(R.id.tv_zip_code);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_country = (TextView) findViewById(R.id.tv_country);
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        //USER DEFINED FIELDS
        tv_udfname = (TextView) findViewById(R.id.tv_udfname);

        //Button
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_payment_method = (Button) findViewById(R.id.btn_payment_method);

        btn_cancel.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_payment_method.setOnClickListener(this);

    }

    public void settingFields(CustomerResponse response) {

        //Customer OVERVIEW
        toolbar_title.setText("Customer Id : " + response.getCustomerId());
        tv_customer_id.setText("" + response.getCustomerId());
        tv_first_name.setText("" + response.getFirstName());
        tv_last_name.setText("" + response.getLastName());
        tv_email.setText("" + response.getEmail());

        if (response.getCompany() != null) {
            tv_company.setText("" + response.getCompany());
        }
        if (response.getPhone() != null) {
            tv_phone.setText("" + response.getPhone());
        }
        tv_notes.setText("" + response.getNotes());
        if (response.isSendEmailReceipts() == true)
            tv_send_email_address.setText("YES");
        else
            tv_send_email_address.setText("NO");

        //ADDRESS
        if (response.getAddress() != null) {

            if (response.getAddress().getLine1() != null) {
                tv_line_one.setText("" + response.getAddress().getLine1());
            }

            if (response.getAddress().getCity() != null) {
                tv_city.setText("" + response.getAddress().getCity());
            }

            if (response.getAddress().getState() != null) {
                tv_state.setText("" + response.getAddress().getState());
            }

            if (response.getAddress().getZip() != null) {
                tv_zip_code.setText("" + response.getAddress().getZip());
            }

            if (response.getAddress().getCountry() != null) {
                tv_country.setText("" + response.getAddress().getCountry());
            }

        }

        //USER DEFINED FIELDS
        if (response.getUserDefinedFields() != null) {
            try {
                JSONObject menu = new JSONObject(response.getUserDefinedFields().toString());
                if (menu.length() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int count = menu.length();
                    for (int i = 1; i <= count; i++) {
                        stringBuilder.append("User Defined Field #" + i + ":\t\t" + menu.getString("UDF" + i) + "\n");
                    }
                    tv_udfname.setText(stringBuilder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_edit:
                Intent update = new Intent(this, UpdateCustomer.class);
                startActivity(update);
                finish();
                break;

            case R.id.btn_delete:
                break;

            case R.id.btn_payment_method:
                break;

            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}