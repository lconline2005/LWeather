package com.example.lyx.lweather.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lyx.lweather.R;
import com.example.lyx.lweather.adapter.DailyRecyclerAdapter;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;
import com.example.lyx.lweather.network.service.IHeaderImageService;
import com.example.lyx.lweather.network.service.IWeatherService;
import com.example.lyx.lweather.utils.DoubleClickExit;
import com.example.lyx.lweather.utils.LogUtil;
import com.example.lyx.lweather.utils.Params;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.example.lyx.lweather.utils.Params.BASE_URL;
import static com.example.lyx.lweather.utils.Params.WEATHERPROKEY;
import static com.example.lyx.lweather.utils.Utility.GetInfoFromSP;
import static com.example.lyx.lweather.utils.Utility.PutInfoToSP;
import static com.example.lyx.lweather.utils.Utility.SaveByFastJson;
import static com.example.lyx.lweather.utils.Utility.getBeanByFastJson;

public class WeatherActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private final String TAG = "WeatherActivity";
    NavigationView navView;
    ImageButton chooseButton;
    private TextView tempNow, windDir, windSc, condText;
    private TextView titleCountyName, titleUpdateTime, apiText, pm25Text, airqulText;
    private TextView comfortContent, washcarContent, dressContent, sportContent, hufuContent;
    private SwipeRefreshLayout swipeRefreshLayout;
    DrawerLayout weatherDrawerLayout;
    ImageView headerImage;
    RecyclerView dailyRecycler;
    List<CountyWeatherEntity.HeWeatherBean.DailyForecastBean> listDailyWeather;
    String intentWeatherid;//intent里面的weatherid
    String weatherId;//SP里面的weatherid


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        findViews();
        //判断header背景图url是否存在SP
        if (GetInfoFromSP(this, "imageurl") != null) {
            Glide.with(this).load(GetInfoFromSP(this, "imageurl")).placeholder(R.mipmap.headback).into(headerImage);
        } else {
            GetHeaderImage();
        }
        //判断天气信息是否存在SP
        Object prefWeather = getBeanByFastJson(WeatherActivity.this, "weather", CountyWeatherEntity.class);
        weatherId = GetInfoFromSP(this, "weatherid");
        intentWeatherid=(getIntent().getStringExtra("weatherid"));
        if (prefWeather != null) {
            if (intentWeatherid != null) {
                RequestWeather(intentWeatherid);
            } else {
                CountyWeatherEntity countyWeather = (CountyWeatherEntity) prefWeather;
                ShowWeatherInfo(countyWeather);
            }
        } else {
            RequestWeather(intentWeatherid);
        }

    }


    public void findViews() {
        //侧边栏
        navView = (NavigationView) findViewById(R.id.nav_view);
        chooseButton = (ImageButton) findViewById(R.id.showNav);
        chooseButton.setOnClickListener(this);

        navView.setNavigationItemSelectedListener(this);
        weatherDrawerLayout = (DrawerLayout) findViewById(R.id.activity_weather);
        headerImage = (ImageView) findViewById(R.id.headerimage);
        dailyRecycler = (RecyclerView) findViewById(R.id.dailyrecycler);
        //suggestion
        comfortContent = (TextView) findViewById(R.id.comfort_content);
        washcarContent = (TextView) findViewById(R.id.washcar_content);
        dressContent = (TextView) findViewById(R.id.dress_content);
        sportContent = (TextView) findViewById(R.id.sport_content);
        hufuContent = (TextView) findViewById(R.id.hufu_content);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.weatherrefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.toolbarbackground));
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestWeather(GetInfoFromSP(WeatherActivity.this, "weatherid"));
                GetHeaderImage();
            }
        });
        //title
        titleCountyName = (TextView) findViewById(R.id.countyname);
        titleUpdateTime = (TextView) findViewById(R.id.updatetime_text);
        //airqulty
        apiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        airqulText = (TextView) findViewById(R.id.airqul_text);
        //header
        tempNow = (TextView) findViewById(R.id.temp_text);
        windDir = (TextView) findViewById(R.id.winddir_text);
        windSc = (TextView) findViewById(R.id.windsc_text);
        condText = (TextView) findViewById(R.id.cond_text);
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
                Boolean isSaved = SaveByFastJson(WeatherActivity.this, "weather", response.body());

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
        swipeRefreshLayout.setRefreshing(false);
    }


    public void ShowWeatherInfo(CountyWeatherEntity weatherInfo) {
        //显示title
        titleCountyName.setText(weatherInfo.getHeWeather().get(0).getBasic().getCity());
        titleUpdateTime.setText(weatherInfo.getHeWeather().get(0).getBasic().getUpdate().getUtc());

        //显示空气指数
        apiText.setText(weatherInfo.getHeWeather().get(0).getAqi().getCity().getAqi());
        pm25Text.setText(weatherInfo.getHeWeather().get(0).getAqi().getCity().getPm25());
        String qulty = weatherInfo.getHeWeather().get(0).getAqi().getCity().getQlty();
        airqulText.setText("空气质量：" + qulty);
        /*可增加根据空气质量改变字体颜色*/
//        if (qulty.equals("优")) {
//        } else if (qulty.equals("良")) {
//        }

        //显示header
        tempNow.setText(weatherInfo.getHeWeather().get(0).getNow().getTmp() + "℃");
        windDir.setText(weatherInfo.getHeWeather().get(0).getNow().getWind().getDir());
        windSc.setText(weatherInfo.getHeWeather().get(0).getNow().getWind().getSc() + "级");
        condText.setText(weatherInfo.getHeWeather().get(0).getNow().getCond().getTxt());

        //显示一周天气预报
        listDailyWeather = weatherInfo.getHeWeather().get(0).getDaily_forecast();
        dailyRecycler.setLayoutManager(new LinearLayoutManager(this));
        //设置分割线后下拉刷新会造成item高度增加
//        dailyRecycler.addItemDecoration(new LinearItemDecration(this, R.drawable.item_driver_line));
        DailyRecyclerAdapter dailyRecyclerAdapter = new DailyRecyclerAdapter(this, listDailyWeather, R.layout.dailyrecycler_item);
        dailyRecycler.setAdapter(dailyRecyclerAdapter);

        //显示生活小贴士
        comfortContent.setText(weatherInfo.getHeWeather().get(0).getSuggestion().getComf().getTxt());
        washcarContent.setText(weatherInfo.getHeWeather().get(0).getSuggestion().getCw().getTxt());
        dressContent.setText(weatherInfo.getHeWeather().get(0).getSuggestion().getDrsg().getTxt());
        sportContent.setText(weatherInfo.getHeWeather().get(0).getSuggestion().getSport().getTxt());
        hufuContent.setText(weatherInfo.getHeWeather().get(0).getSuggestion().getUv().getTxt());
    }


    //侧边栏
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_loac:
                break;
            case R.id.nav_city:
                Intent intent = new Intent(this, ChooseCityActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.nav_multi_cities:
                break;
            case R.id.nav_set:
                break;
            case R.id.nav_about:
                break;

        }
        return false;
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showNav:
                weatherDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }

    }


    //退出
    @Override
    public void onBackPressed() {
        if (weatherDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            weatherDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!DoubleClickExit.check()) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }


    public String getUpdateTime(String dateYear) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String monthDay = null;
        try {
            date = sdf.parse(dateYear);
            // 获取日期实例
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //月份是从0开始
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            monthDay = month + "-" + day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return monthDay;
    }


}
