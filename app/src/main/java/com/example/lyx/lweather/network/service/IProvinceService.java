package com.example.lyx.lweather.network.service;

import com.example.lyx.lweather.network.entity.ProvinceEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/6/13.
 */

public interface IProvinceService {
    @GET("china")
//    Call<List<ProvinceEntity>> getProvince();
    Call<String> getProvince();
}
