package com.kalcodes.fooddelivery.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.kalcodes.fooddelivery.R;
import com.kalcodes.fooddelivery.eventbus.MyUpdateCartEvent;
import com.kalcodes.fooddelivery.model.CartModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {

    private final Context context;
    private final List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    public static class MyCartViewHolder extends RecyclerView.ViewHolder{
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.btnMinus)
        ImageView btnMinus;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.btnPlus)
        ImageView btnPlus;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.btnDelete)
        ImageView btnDelete;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.imageView)
        ImageView imageView;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtName)
        TextView txtName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtPrice)
        TextView txtPrice;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtQuantity)
        TextView txtQuantity;



        Unbinder unbinder;
        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        Glide.with(context)
                .load(cartModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("$").append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        //Event
        holder.btnMinus.setOnClickListener(v -> minusCartItem(holder,cartModelList.get(position)));

        holder.btnPlus.setOnClickListener(v -> plusCartItem(holder,cartModelList.get(position)));

        holder.btnDelete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete From Cart")
                    .setMessage("Do you want to delete this food")
                    .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("OK", (dialog12, which) -> {

                        //Temp remove
                        notifyItemRemoved(position);



                        deleteFromFirebase(cartModelList.get(position));
                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });
    }

    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(Void -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    private void plusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        cartModel.setQuantity(cartModel.getQuantity()+1);
        cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

        //Update quantity
        holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }

    private void minusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        if (cartModel.getQuantity() > 1){
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            //Update quantity
            holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }

    }

    private void updateFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(Void -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }


}
