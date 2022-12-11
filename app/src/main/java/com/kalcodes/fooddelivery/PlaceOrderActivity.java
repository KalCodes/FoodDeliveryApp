package com.kalcodes.fooddelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PlaceOrderActivity extends AppCompatActivity {
    ImageView back;
    AppCompatButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        back = findViewById(R.id.back);
        button = findViewById(R.id.order);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Fragment fragment = new CartFragment();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.container,fragment).commit();

                Intent intent = new Intent(PlaceOrderActivity.this, CartFragment.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceOrderActivity.this,PendingFragment.class));
            }
        });


    }
}