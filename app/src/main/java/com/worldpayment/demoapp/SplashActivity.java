package com.worldpayment.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView splshIV;
    TextView movingTV;
    private static AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splshIV = (ImageView) findViewById(R.id.splshIV);
        movingTV = (TextView) findViewById(R.id.movingTV);

        Animation blinking = AnimationUtils.loadAnimation(this, R.anim.blink);
        movingTV.startAnimation(blinking);

        Animation zooming = AnimationUtils.loadAnimation(this, R.anim.zoom);
        splshIV.startAnimation(zooming);

//        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotation);
//        splshIV.startAnimation(rotate);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Navigation.class);
                startActivity(intent);
                finish();
                //     startActivityForResult(ScanningActivity.intent(SplashActivity.this), REQUEST_SCAN);

            }
        }, 5 * 1000);
    }
}
