package com.example.lyx.lweather.network.service;

import com.example.lyx.lweather.network.entity.CountyEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/14.
 */

public interface ICountyService {
    @GET("china/{provinceId}/{cityId}")
    Call<List<CountyEntity>> getCounty(@Path("provinceId") int provinceid, @Path("cityId") int cityid);
}
