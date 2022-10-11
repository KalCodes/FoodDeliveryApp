package com.kalcodes.fooddelivery.listener;

import com.kalcodes.fooddelivery.model.FoodModel;

import java.util.List;

public interface IFoodLoadListener {
    void onFoodLoadSuccess(List<FoodModel> foodModelList);
    void onFoodLoadFailed(String message);
}
