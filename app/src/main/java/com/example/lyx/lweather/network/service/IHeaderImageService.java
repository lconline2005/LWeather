package com.example.lyx.lweather.network.service;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator
 * on 2017/6/22.
 * 修改备注：
 */

public interface IHeaderImageService {
    @GET("bing_pic")
    Call<String> getImageUrl();
}
