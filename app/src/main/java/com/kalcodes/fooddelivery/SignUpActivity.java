package com.kalcodes.fooddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import com.google.firebase.database.FirebaseDatabase;
import com.kalcodes.fooddelivery.model.User;


public class SignUpActivity extends AppCompatActivity {

     FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText fullName = findViewById(R.id.fullName);
        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);

        final AppCompatButton signUpBtn = findViewById(R.id.signUpBtn);
        final TextView signInBtn = findViewById(R.id.signInBtn);

        mAuth=FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullNameTxt = fullName.getText().toString();
                String emailTxt = email.getText().toString();
                String phoneTxt = phone.getText().toString();
                String passwordTxt = password.getText().toString();

                if(fullNameTxt.isEmpty()){
                    fullName.setError("Full name is required");
                    fullName.requestFocus();
                    return;

                }else if (emailTxt.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;

                }else if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                    email.setError("Please provide valid email");
                    email.requestFocus();
                    return;

                }else if (phoneTxt.isEmpty()){
                    phone.setError("Phone number is required");
                    phone.requestFocus();
                    return;

                }else if (passwordTxt.isEmpty()){
                    password.setError("Password is required");
                    password.requestFocus();
                    return;

                }else if (password.length() < 6){
                    password.setError("Min password length should be 6 ");
                    password.requestFocus();
                    return;

                }else {
                    mAuth.createUserWithEmailAndPassword(emailTxt,passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                User user = new User(fullNameTxt,emailTxt,phoneTxt);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignUpActivity.this, "User has been registered successfully", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(SignUpActivity.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(SignUpActivity.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}