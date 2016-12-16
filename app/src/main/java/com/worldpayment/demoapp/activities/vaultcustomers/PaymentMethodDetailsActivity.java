package com.worldpayment.demoapp.activities.vaultcustomers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldpay.library.domain.VaultPaymentMethod;
import com.worldpay.library.enums.ResponseCode;
import com.worldpay.library.views.WPTextView;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpay.library.webservices.services.paymentmethods.DeletePaymentMethodRequest;
import com.worldpay.library.webservices.services.paymentmethods.PaymentMethodResponse;
import com.worldpay.library.webservices.tasks.PaymentMethodDeleteTask;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;
import com.worldpayment.demoapp.utility.TokenUtility;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDetailsActivity extends WorldBaseActivity implements View.OnClickListener {

    WPTextView tv_customer_id;
    RecyclerView recycler_view;
    TextView error;
    String responseFromIntent;
    List<VaultPaymentMethod> paymentMethodsList = new ArrayList<VaultPaymentMethod>();
    Button create_payment_account_button, btn_cancel;
    LinearLayout linearLayoutButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method_details);
        setActivity(PaymentMethodDetailsActivity.this);
        mappingViews();

        if (getIntent().getExtras() != null) {
            responseFromIntent = getIntent().getExtras().getString("response");
            Gson gson = new Gson();
            CustomerResponse customerResponse = gson.fromJson(responseFromIntent, CustomerResponse.class);

            VaultPaymentMethod[] vaultPaymentMethods = customerResponse.getPaymentMethods();

            for (int i = 0; i < vaultPaymentMethods.length; i++) {
                paymentMethodsList.add(vaultPaymentMethods[i]);
            }

            if (paymentMethodsList.size() < 1) {
                recycler_view.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                linearLayoutButtons.setVisibility(View.VISIBLE);
                error.setText("" + getResources().getString(R.string.noPaymentID));
            } else {
                recycler_view.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                linearLayoutButtons.setVisibility(View.GONE);
                PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethodsList);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
                recycler_view.setLayoutManager(layoutManager);
                recycler_view.setHasFixedSize(true);
                recycler_view.setItemAnimator(new DefaultItemAnimator());
                recycler_view.setAdapter(paymentMethodAdapter);
            }
        }
    }

    public void mappingViews() {
        tv_customer_id = (WPTextView) findViewById(R.id.tv_customer_id);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        create_payment_account_button = (Button) findViewById(R.id.create_payment_account_button);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        linearLayoutButtons = (LinearLayout) findViewById(R.id.linearLayoutButtons);

        error = (TextView) findViewById(R.id.error);
        create_payment_account_button.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        // tv_customer_id.setText("" + "");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_payment_account_button:
                Intent createPayment = new Intent(PaymentMethodDetailsActivity.this, CreatePaymentMethod.class);
                startActivity(createPayment);
                break;

            case R.id.btn_cancel:
                finish();

            default:
                break;
        }
    }


    public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.UserViewHolder> {

        List<VaultPaymentMethod> paymentMethodList;
        Context context;

        public PaymentMethodAdapter(Context context, List<VaultPaymentMethod> paymentMethodsList) {
            this.context = context;
            this.paymentMethodList = paymentMethodsList;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_cards_row, null);
            UserViewHolder userViewHolder = new UserViewHolder(view);
            return userViewHolder;

        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            VaultPaymentMethod item = paymentMethodList.get(position);

            //   holder.payment_id.setText("" + item.getClass().toString());
            holder.btn_delete.setTag(position);
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeletePaymentMethodRequest deletePaymentMethodRequest = new DeletePaymentMethodRequest();
                    TokenUtility.populateRequestHeaderFields(deletePaymentMethodRequest, context);
                    deletePaymentMethodRequest.setCustomerId("50000037");
                    deletePaymentMethodRequest.setPaymentMethodId("1");
                    new PaymentMethodDeleteTask(deletePaymentMethodRequest) {
                        ProgressDialog progressDialog;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressDialog = new ProgressDialog(context);
                            startProgressBar(progressDialog, "Deleting account...");
                        }

                        @Override
                        protected void onPostExecute(PaymentMethodResponse paymentMethodResponse) {
                            super.onPostExecute(paymentMethodResponse);

                            if (paymentMethodResponse != null) {

                                if (paymentMethodResponse.getResponseCode() == ResponseCode.APPROVED) {
                                    showDialogView(context.getResources().getString(R.string.success), paymentMethodResponse.getResponseMessage(), context);

                                } else {
                                    showDialogView(context.getResources().getString(R.string.error), paymentMethodResponse.getResponseMessage(), context);
                                }
                            } else {
                                showDialogView(context.getResources().getString(R.string.error), "Web Service Error!", context);
                            }
                        }
                    };
                }
            });
        }

        @Override
        public int getItemCount() {
            return paymentMethodList.size();
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {

            WPTextView payment_id, card_no, expiration_date, pin_block;
            Button btn_edit, btn_delete;

            public UserViewHolder(View itemView) {
                super(itemView);

                payment_id = (WPTextView) itemView.findViewById(R.id.payment_id);
                card_no = (WPTextView) itemView.findViewById(R.id.card_no);
                expiration_date = (WPTextView) itemView.findViewById(R.id.expiration_date);
                pin_block = (WPTextView) itemView.findViewById(R.id.pin_block);

                btn_edit = (Button) itemView.findViewById(R.id.btn_edit);
                btn_delete = (Button) itemView.findViewById(R.id.btn_delete);


            }
        }
    }
}
