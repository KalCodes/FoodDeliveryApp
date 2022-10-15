package com.kalcodes.fooddelivery.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kalcodes.fooddelivery.R;
import com.kalcodes.fooddelivery.eventbus.MyUpdateCartEvent;
import com.kalcodes.fooddelivery.listener.ICartLoadListener;
import com.kalcodes.fooddelivery.listener.IRecyclerViewClickListener;
import com.kalcodes.fooddelivery.model.CartModel;
import com.kalcodes.fooddelivery.model.FoodModel;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyFoodViewHolder> {

    private Context context;
    private List<FoodModel> foodModelList;
    private ICartLoadListener iCartLoadListener;

    public MyFoodAdapter(Context context, List<FoodModel> foodModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.iCartLoadListener = iCartLoadListener;
    }


    @NonNull
    @Override
    public MyFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyFoodViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyFoodViewHolder holder, int position) {
        Glide.with(context)
                .load(foodModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("$").append(foodModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(foodModelList.get(position).getName()));

        holder.setListener((view, adapterPosition) -> {
            addToCart(foodModelList.get(position));
        });
    }

    private void addToCart(FoodModel foodModel) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");

        userCart.child(foodModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){ // If user already have item in cart
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);

                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("quantity",cartModel.getQuantity());
                            updateData.put("totalPrice",cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

                            userCart.child(foodModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(unused -> {
                                        iCartLoadListener.onCartLoadFailed("Add To Cart Success");
                                    }).addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()))
                            ;


                        }
                        else { // If  item not have in cart, add new
                            CartModel cartModel = new CartModel();
                            cartModel.setName(foodModel.getName());
                            cartModel.setImage(foodModel.getImage());
                            cartModel.setKey(foodModel.getKey());
                            cartModel.setPrice(foodModel.getPrice());
                            cartModel.setQuantity(1);

                            cartModel.setTotalPrice(Float.parseFloat(foodModel.getPrice()));

                            userCart.child(foodModel.getKey())
                                    .setValue(cartModel)
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        EventBus.getDefault().postSticky( new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();

    }


    public class MyFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;

        Unbinder unbinder;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }



        public MyFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerClick(v,getAdapterPosition());
        }
    }




}
