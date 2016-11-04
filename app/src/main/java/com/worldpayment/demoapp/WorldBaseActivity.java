package com.worldpayment.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity;
import com.worldpayment.demoapp.activities.refundvoid.RefundVoidViewActivity;
import com.worldpayment.demoapp.activities.settlement.ActivitySettlement;
import com.worldpayment.demoapp.activities.vaultcustomers.VaultOperations;
import com.worldpayment.demoapp.utility.KeyboardUtility;

public class WorldBaseActivity extends AppCompatActivity {

    private WorldBaseActivity worldBaseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //  setUpNavigationDrawer();
        setUpToolBar();
    }


    public void setActivity(WorldBaseActivity worldBaseActivity) {
        this.worldBaseActivity = worldBaseActivity;
    }

    public WorldBaseActivity getActivity() {
        return worldBaseActivity;
    }

    public void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (null != toolbar) {

            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.logo_launcher);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            if (worldBaseActivity instanceof RefundVoidViewActivity) {
                toolbar.setTitle("Refund/Void");
            }
            if (worldBaseActivity instanceof CreditDebitActivity) {
                toolbar.setTitle("Credit/Debit");
            }

            if (worldBaseActivity instanceof ActivitySettlement) {
                toolbar.setTitle("Settlement");
            }

            if (worldBaseActivity instanceof VaultOperations) {
                toolbar.setTitle("Vault");
            }


            navigationView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent ev) {
                    KeyboardUtility.closeKeyboard(getApplicationContext(), view);
                    return false;
                }
            });

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                // This method will trigger on item Click of navigation menu
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    if (menuItem.isChecked())
                        menuItem.setChecked(false);
                    else
                        menuItem.setChecked(true);

                    //Closing member_menu_items on item click
                    drawer.closeDrawers();

                    //Check to see which item was being clicked and perform appropriate action
                    switch (menuItem.getItemId()) {

                        case R.id.home:
                            drawer.closeDrawers();
                            Intent home = new Intent(WorldBaseActivity.this, Navigation.class);
                            startActivity(home);
                            finish();
                            return true;

                        case R.id.debit_credit:
                            Intent intent = new Intent(WorldBaseActivity.this, CreditDebitActivity.class);
                            startActivity(intent);
                            finish();
                            return true;

                        case R.id.refund_void:
                            Intent refund_void = new Intent(WorldBaseActivity.this, RefundVoidViewActivity.class);
                            startActivity(refund_void);
                            finish();

                            return true;

                        case R.id.settlement:
                            Intent settlement = new Intent(WorldBaseActivity.this, ActivitySettlement.class);
                            startActivity(settlement);
                            finish();
                            return true;

                        case R.id.vault:
                            Intent vault = new Intent(WorldBaseActivity.this, VaultOperations.class);
                            startActivity(vault);
                            finish();
                            return true;


                        default:
                            return true;

                    }
                }
            });


            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };

            //Setting the actionbarToggle to member_menu_items layout
            drawer.setDrawerListener(actionBarDrawerToggle);
            //calling sync state is necessay or else your hamburger icon wont show up
            actionBarDrawerToggle.syncState();

        }
    }

    public static void startProgressBar(ProgressDialog masterProgress, String message) {
        masterProgress.setMessage(message);
        masterProgress.setIndeterminate(true);
        masterProgress.setCancelable(false);
        masterProgress.show();
    }

    public static void dismissProgressBar(ProgressDialog masterProgress) {
        masterProgress.dismiss();
    }

    public static void showSuccessDialog(final String titleStr, final String messageStr, final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.master_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);


        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        TextView message = (TextView) dialogSignature.findViewById(R.id.message);

        if (titleStr.toString().trim().equals("ERROR!")) {
            title.setTextColor(Color.parseColor("#f11e15"));
        } else if (titleStr.toString().trim().equals("SUCCESS")) {
            title.setTextColor(Color.parseColor("#007867"));
        }
        title.setText(titleStr);
        message.setText(messageStr);

        Button dialog_btn_negative = (Button) dialogSignature.findViewById(R.id.dialog_btn_negative);
        dialog_btn_negative.setVisibility(View.GONE);
        Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);

        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    public static void buttonEnabled(Button btn1, Button btn2, int flag) {
        if (flag == 0) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundResource(R.drawable.button_shap);

            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundResource(R.drawable.button_disable);
        } else if (flag == 1) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundResource(R.drawable.button_shap);

            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundResource(R.drawable.button_disable);
        }
        if (flag == 2) {
            btn1.setTextColor(Color.WHITE);
            btn1.setBackgroundResource(R.drawable.button_shap);

            btn2.setTextColor(Color.WHITE);
            btn2.setBackgroundResource(R.drawable.button_disable);
        }
    }
}
