package com.kalcodes.fooddelivery;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kalcodes.fooddelivery.adapter.MyFoodAdapter;
import com.kalcodes.fooddelivery.eventbus.MyUpdateCartEvent;
import com.kalcodes.fooddelivery.listener.ICartLoadListener;
import com.kalcodes.fooddelivery.listener.IFoodLoadListener;
import com.kalcodes.fooddelivery.model.CartModel;
import com.kalcodes.fooddelivery.model.FoodModel;
import com.kalcodes.fooddelivery.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements IFoodLoadListener, ICartLoadListener {

    public HomeFragment() {
        // Required empty public constructor
    }
    View view;
    RecyclerView recyclerFood ;
    RelativeLayout mainLayout ;

    ImageSlider imageSlider;

    IFoodLoadListener foodLoadListener;
    ICartLoadListener cartLoadListener;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdate(MyUpdateCartEvent event){
        countCartItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        requireActivity().setTitle("ET-Food");

        recyclerFood = view.findViewById(R.id.recyclerFood);
         mainLayout = view.findViewById(R.id.mainLayout);
//
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageSlider = view.findViewById(R.id.imageSlider);
        final List<SlideModel> remoteimages = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Slider")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            remoteimages.add(new SlideModel(data.child("url").getValue().toString(), ScaleTypes.FIT ));

                            imageSlider.setImageList(remoteimages,ScaleTypes.FIT);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            init();
            loadFoodFromFirebase();
            countCartItem();



        return view;
    }

    private void loadFoodFromFirebase() {
        List<FoodModel> foodModels = new ArrayList<>();
        FirebaseDatabase.getInstance().
                getReference("Drink")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot foodSnapshot:snapshot.getChildren()){
                                FoodModel foodModel = foodSnapshot.getValue(FoodModel.class);
                                foodModel.setKey(foodSnapshot.getKey());
                                foodModels.add(foodModel );
                            }
                            foodLoadListener.onFoodLoadSuccess(foodModels);
                        }
                        else
                            foodLoadListener.onFoodLoadFailed("Can't find Food");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        foodLoadListener.onFoodLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){
        ButterKnife.bind((Activity) this.getContext());

        foodLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2);
        recyclerFood.setLayoutManager(gridLayoutManager);
//        recyclerFood.addItemDecoration(new SpaceItemDecoration());

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2,LinearLayoutManager.VERTICAL,false);



//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerFood.setLayoutManager(layoutManager);

    }

    @Override
    public void onFoodLoadSuccess(List<FoodModel> foodModelList) {
        MyFoodAdapter adapter = new MyFoodAdapter(this.getContext(),foodModelList,cartLoadListener);
        recyclerFood.setAdapter(adapter);
    }

    @Override
    public void onFoodLoadFailed(String message) {
        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for (CartModel cartModel: cartModelList)
            cartSum += cartModel.getQuantity();
        //badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cartSnapshot:snapshot.getChildren()){
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}