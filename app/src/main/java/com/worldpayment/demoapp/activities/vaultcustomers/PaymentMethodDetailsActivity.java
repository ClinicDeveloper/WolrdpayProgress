package com.worldpayment.demoapp.activities.vaultcustomers;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.worldpay.library.domain.VaultPaymentMethod;
import com.worldpay.library.views.WPTextView;
import com.worldpay.library.webservices.services.customers.CustomerResponse;
import com.worldpayment.demoapp.R;
import com.worldpayment.demoapp.WorldBaseActivity;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDetailsActivity extends WorldBaseActivity {

    WPTextView tv_customer_id;
    RecyclerView recycler_view;
    String responseFromIntent;
    List<VaultPaymentMethod> paymentMethodsList = new ArrayList<VaultPaymentMethod>();

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

            PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethodsList);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(layoutManager);
            recycler_view.setHasFixedSize(true);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(paymentMethodAdapter);
        }
    }

    public void mappingViews() {
        tv_customer_id = (WPTextView) findViewById(R.id.tv_customer_id);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        // tv_customer_id.setText("" + "");

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
