package com.example.lyx.lweather.network.service;

import com.example.lyx.lweather.network.entity.CityEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/14.
 */

public interface ICityService {
    @GET("china/{province}")
    Call<List<CityEntity>> getCity(@Path("province") int provinceId);
}
