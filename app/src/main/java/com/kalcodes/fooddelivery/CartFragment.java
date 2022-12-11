package com.kalcodes.fooddelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kalcodes.fooddelivery.adapter.MyCartAdapter;
import com.kalcodes.fooddelivery.listener.ICartLoadListener;
import com.kalcodes.fooddelivery.model.CartModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class CartFragment extends Fragment implements ICartLoadListener {

    public CartFragment() {
        // Required empty public constructor
    }
    LinearLayout layoutPay;
    RecyclerView recyclerCart;
    RecyclerView mainLayout;
    TextView txtTotal;
    Button button;
    View view;

    ICartLoadListener cartLoadListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);





        button = view.findViewById(R.id.pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlaceOrderActivity.class);
                startActivity(intent);
            }
        });

        requireActivity().setTitle("Cart");


        layoutPay = view.findViewById(R.id.layoutPay);
        recyclerCart = view.findViewById(R.id.recycler_cart);
        mainLayout = view.findViewById(R.id.mainLayout);
        txtTotal = view.findViewById(R.id.txtTotal);

        init();
        loadCartFromFirebase();


        return view;
    }

    private void loadCartFromFirebase() {
        List<CartModel> cartModels = new ArrayList<>();


        FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot cartSnapshot:snapshot.getChildren()){
                                CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                                cartModel.setKey(cartModel.getKey());
                                cartModels.add(cartModel);
                            }
                            cartLoadListener.onCartLoadSuccess(cartModels);
                        }
                        else
                            cartLoadListener.onCartLoadFailed("Cart empty");
                        //layoutPay.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    private void init() {
        ButterKnife.bind((Activity) this.getContext());

        cartLoadListener = this;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this.getContext(),layoutManager.getOrientation()));
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        double sum = 0;
        for (CartModel cartModel:cartModelList){
            sum+=cartModel.getTotalPrice();
        }
        txtTotal.setText(new StringBuilder("$").append(sum));
        MyCartAdapter adapter = new MyCartAdapter(this.getContext(),cartModelList);
        recyclerCart.setAdapter(adapter);

    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
        layoutPay.setVisibility(View.GONE);
    }

}