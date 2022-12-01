package com.kalcodes.fooddelivery;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar();


        mAuth = FirebaseAuth.getInstance();
//        getSupportActionBar(),setTitle("ghg");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }





        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

}