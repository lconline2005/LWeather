package com.example.lyx.lweather.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lyx.lweather.CommonAdapter.LinearItemDecration;
import com.example.lyx.lweather.CommonAdapter.RecyclerViewCommonAdapter;
import com.example.lyx.lweather.R;
import com.example.lyx.lweather.adapter.DailyRecyclerAdapter;
import com.example.lyx.lweather.network.entity.CityEntity;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;
import com.example.lyx.lweather.network.service.IHeaderImageService;
import com.example.lyx.lweather.network.service.IWeatherService;
import com.example.lyx.lweather.utils.LogUtil;
import com.example.lyx.lweather.utils.Params;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.example.lyx.lweather.utils.Params.BASE_URL;
import static com.example.lyx.lweather.utils.Params.HEADERIMAGE_URL;
import static com.example.lyx.lweather.utils.Params.WEATHERKEY;
import static com.example.lyx.lweather.utils.Params.WEATHERPROKEY;
import static com.example.lyx.lweather.utils.Utility.GetInfoFromSP;
import static com.example.lyx.lweather.utils.Utility.PutInfoToSP;
import static com.example.lyx.lweather.utils.Utility.PutWeatherToSP;

public class WeatherActivity extends BaseActivity {
    private final String TAG = "WeatherActivity";
    private String weatherId;
    ImageView headerImage;
    RecyclerView dailyRecycler;
    List<CountyWeatherEntity.HeWeatherBean.DailyForecastBean> listDailyWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        fingViews();
        //判断header背景图url是否存在SP
        if (GetInfoFromSP(this, "imageurl") != null) {
            Glide.with(this).load(GetInfoFromSP(this, "imageurl")).placeholder(R.mipmap.headback).into(headerImage);
        } else {
            GetHeaderImage();
        }
        //判断天气信息是否存在SP
        String prefWeather = GetInfoFromSP(this, "weather");
        if (prefWeather != null) {
            CountyWeatherEntity countyWeather = new Gson().fromJson(GetInfoFromSP(WeatherActivity.this, "weather"), CountyWeatherEntity.class);
            ShowWeatherInfo(countyWeather);
        } else {
            RequestWeather(getIntent().getStringExtra("weatherid"));
        }

    }

    public CountyWeatherEntity getweather(String weather){
        try {
            JSONObject jsonObject=new JSONObject(weather);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weatherContent,CountyWeatherEntity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void fingViews() {
        headerImage = (ImageView) findViewById(R.id.headerimage);
        dailyRecycler = (RecyclerView) findViewById(R.id.dailyrecycler);
    }

    public void RequestWeather(String weatherId) {
        //取到数据
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IWeatherService weatherService = retrofit.create(IWeatherService.class);
        Call<CountyWeatherEntity> call = weatherService.getWeather(weatherId, WEATHERPROKEY);
        call.enqueue(new Callback<CountyWeatherEntity>() {
            @Override
            public void onResponse(Call<CountyWeatherEntity> call, Response<CountyWeatherEntity> response) {
                LogUtil.d(TAG, "weatherresponse==>" + response.body());
                //显示数据
                ShowWeatherInfo(response.body());
                //存到sharedPreference
                PutWeatherToSP(WeatherActivity.this, "weather", response.body());
                LogUtil.d(TAG, "存入的weather" + response.body());
            }

            @Override
            public void onFailure(Call<CountyWeatherEntity> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, "天气信息加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GetHeaderImage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Params.HEADERIMAGE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        IHeaderImageService headerImageService = retrofit.create(IHeaderImageService.class);
        Call<String> call = headerImageService.getImageUrl();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogUtil.d(TAG, "图片URL" + response.body());
                Glide.with(WeatherActivity.this)
                        .load(response.body())
                        .placeholder(R.mipmap.headback)
                        .into(headerImage);
                PutInfoToSP(WeatherActivity.this, "imageurl", response.body());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void ShowWeatherInfo(CountyWeatherEntity weatherInfo) {
        listDailyWeather = weatherInfo.getHeWeather().get(0).getDaily_forecast();

        //显示一周天气预报
        dailyRecycler.setLayoutManager(new LinearLayoutManager(this));
        dailyRecycler.addItemDecoration(new LinearItemDecration(this, R.drawable.item_driver_line));
        DailyRecyclerAdapter dailyRecyclerAdapter = new DailyRecyclerAdapter(this, listDailyWeather, R.layout.dailyrecycler_item);
        dailyRecycler.setAdapter(dailyRecyclerAdapter);
    }


}
