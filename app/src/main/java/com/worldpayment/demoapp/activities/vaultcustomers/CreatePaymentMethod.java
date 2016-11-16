package com.worldpayment.demoapp.activities.vaultcustomers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.worldpay.library.views.WPFormEditText;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

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
                break;

            case R.id.btn_cancel:
                finish();

            default:
                break;
        }
    }
}
