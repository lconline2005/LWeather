package com.example.lyx.lweather.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lyx.lweather.R;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;
import com.example.lyx.lweather.network.service.IWeatherService;
import com.example.lyx.lweather.utils.LogUtil;
import com.example.lyx.lweather.utils.Params;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.Contacts.SettingsColumns.KEY;

public class WeatherActivity extends AppCompatActivity {
    private final String TAG="WeatherActivity";
    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherId=getIntent().getStringExtra("weatherid");
        RequestWeather(weatherId);
    }


    public void RequestWeather(String weatherId){
        // 构建做好相关配置的 OkHttpClient 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Params.WEATHER_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IWeatherService weatherService=retrofit.create(IWeatherService.class);
        Call<CountyWeatherEntity> call=weatherService.getWeather(weatherId,KEY);
        call.enqueue(new Callback<CountyWeatherEntity>() {
            @Override
            public void onResponse(Call<CountyWeatherEntity> call, Response<CountyWeatherEntity> response) {
                LogUtil.d(TAG,"weatherresponse==>"+response.body().toString());
            }

            @Override
            public void onFailure(Call<CountyWeatherEntity> call, Throwable t) {
                Toast.makeText(WeatherActivity.this,"天气信息加载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
