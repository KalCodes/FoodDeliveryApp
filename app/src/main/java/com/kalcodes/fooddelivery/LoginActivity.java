package com.kalcodes.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final TextView signInBtn = findViewById(R.id.signInBtn);
        final TextView signUpBtn = findViewById(R.id.signUpBtn);

        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();



        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();

                if(emailTxt.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;

                }else if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
                    email.setError("Please provide valid email");
                    email.requestFocus();
                    return;

                }else if(passwordTxt.isEmpty()){
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }else if (password.length() < 6){
                    password.setError("Min password length should be 6 ");
                    password.requestFocus();
                    return;

                }else {
                    mAuth.signInWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }else {
                                Toast.makeText(LoginActivity.this, "Failed to login! Please check your Internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent());

                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }
}