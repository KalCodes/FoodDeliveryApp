package com.kalcodes.fooddelivery.listener;

import com.kalcodes.fooddelivery.model.CartModel;
import com.kalcodes.fooddelivery.model.FoodModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
