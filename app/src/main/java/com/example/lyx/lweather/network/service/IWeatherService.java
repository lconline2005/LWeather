package com.example.lyx.lweather.network.service;

import com.example.lyx.lweather.network.entity.CountyWeatherEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator
 * on 2017/6/21.
 * 修改备注：
 */

public interface IWeatherService {
    @GET("weather?")
    Call<CountyWeatherEntity> getWeather(@Query("city") String weatherId,@Query("key") String key);
}
