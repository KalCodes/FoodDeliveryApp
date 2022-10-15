package com.kalcodes.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kalcodes.fooddelivery.model.User;


public class ProfileFragment extends Fragment {

    public ProfileFragment(){

    }

    FirebaseUser user;
    DatabaseReference reference;

    String userID;

    Button logOutBtn;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_profile, container, false);
        logOutBtn = view.findViewById(R.id.logOutBtn);

        requireActivity().setTitle("Profile");

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileFragment.this.getActivity(), LoginActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView userFullName = view.findViewById(R.id.userFullName);
        final TextView userEmail = view.findViewById(R.id.userEmail);
        final TextView userPhone = view.findViewById(R.id.userPhone);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null){
                    String fullname = userProfile.fullName;
                    String email = userProfile.email;
                    String phone = userProfile.phone;

                    userFullName.setText("Full Name "+ fullname);
                    userEmail.setText("Email "+ email);
                    userPhone.setText("Phone " + phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileFragment.this.getActivity(), "Something wrong happened ", Toast.LENGTH_SHORT).show();
            }
        });


         return view;
    }
}