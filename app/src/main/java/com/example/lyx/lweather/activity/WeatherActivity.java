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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.example.lyx.lweather.R;
import com.example.lyx.lweather.adapter.DailyRecyclerAdapter;
import com.example.lyx.lweather.dbase.City;
import com.example.lyx.lweather.dbase.County;
import com.example.lyx.lweather.dbase.Province;
import com.example.lyx.lweather.network.entity.CityEntity;
import com.example.lyx.lweather.network.entity.CountyEntity;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;
import com.example.lyx.lweather.network.service.ICityService;
import com.example.lyx.lweather.network.service.ICountyService;
import com.example.lyx.lweather.network.service.IHeaderImageService;
import com.example.lyx.lweather.network.service.IWeatherService;
import com.example.lyx.lweather.network.service.WeatherUpdateService;
import com.example.lyx.lweather.utils.DoubleClickExit;
import com.example.lyx.lweather.utils.LogUtil;
import com.example.lyx.lweather.utils.Params;
import com.example.lyx.lweather.utils.Utility;

import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.example.lyx.lweather.network.service.WeatherUpdateService.ACTION_NOTIFICATION;
import static com.example.lyx.lweather.utils.Params.BASE_URL;
import static com.example.lyx.lweather.utils.Params.SPWEATHERIDKEY;
import static com.example.lyx.lweather.utils.Params.SPWEATHERKEY;
import static com.example.lyx.lweather.utils.Params.WEATHERPROKEY;
import static com.example.lyx.lweather.utils.Utility.GPSCountyResponse;
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
    ImageView headerImage,condImage;
    RecyclerView dailyRecycler;
    List<CountyWeatherEntity.HeWeatherBean.DailyForecastBean> listDailyWeather;
    String intentWeatherid;//intent里面的weatherid
    String weatherId;//SP里面的weatherid
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private List<County> countyList = null;

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
        Object prefWeather = getBeanByFastJson(WeatherActivity.this, SPWEATHERKEY, CountyWeatherEntity.class);
        weatherId = GetInfoFromSP(this, SPWEATHERIDKEY);
        intentWeatherid = (getIntent().getStringExtra(SPWEATHERIDKEY));
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
        StartAutoUpdate();
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
        condImage= (ImageView) findViewById(R.id.cond_image);
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
                Boolean isSaved = SaveByFastJson(WeatherActivity.this, SPWEATHERKEY, response.body());
                Intent intent = new Intent(ACTION_NOTIFICATION);
                sendBroadcast(intent);
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
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    public void ShowWeatherInfo(CountyWeatherEntity weatherInfo) {

        //显示title
        titleCountyName.setText(weatherInfo.getHeWeather().get(0).getBasic().getCity());
        titleUpdateTime.setText(weatherInfo.getHeWeather().get(0).getBasic().getUpdate().getUtc());


        //6.27后接口数据没有了aqi
        //显示空气指数
//        apiText.setText(weatherInfo.getHeWeather().get(0).getAqi().getCity().getAqi());
//        pm25Text.setText(weatherInfo.getHeWeather().get(0).getAqi().getCity().getPm25());
//        String qulty = weatherInfo.getHeWeather().get(0).getAqi().getCity().getQlty();

//        airqulText.setText("空气质量：" + qulty);
        /*可增加根据空气质量改变字体颜色*/
//        if (qulty.equals("优")) {
//        } else if (qulty.equals("良")) {
//        }

        //显示header
        tempNow.setText(weatherInfo.getHeWeather().get(0).getNow().getTmp() + "℃");
        windDir.setText(weatherInfo.getHeWeather().get(0).getNow().getWind().getDir());
        windSc.setText(weatherInfo.getHeWeather().get(0).getNow().getWind().getSc() + "级");
        condText.setText(weatherInfo.getHeWeather().get(0).getNow().getCond().getTxt());
        condImage.setImageResource(getIcon(weatherInfo.getHeWeather().get(0).getNow().getCond().getCode()));
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
                GetGPSPlace();
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

    //开启自动更新服务
    public void StartAutoUpdate() {
        Intent intent = new Intent(this, WeatherUpdateService.class);
        startService(intent);
    }



    //根据weathercode选择天气图标
    public int getIcon(String weathercode) {
        if (weathercode!=null){
            switch (Integer.valueOf(weathercode)) {
                case 100:
                    return R.mipmap.sun;
                case 101:
                    return R.mipmap.cloud;
                case 102:
                    return R.mipmap.cloud;
                case 103:
                    return R.mipmap.cloudly;
                case 104:
                    return R.mipmap.cloud;
                case 300:
                    return R.mipmap.rain;
                case 211:
                    return R.mipmap.hurri;
                case 212:
                    return R.mipmap.hurri;
                case 213:
                    return R.mipmap.hurri;
                case 301:
                    return R.mipmap.heavyrain;
                case 302:
                    return R.mipmap.rainwithlight;
                case 303:
                    return R.mipmap.rainwithlight;
                case 304:
                    return R.mipmap.raintiwhhail;
                case 305:
                    return R.mipmap.lightrain;
                case 306:
                    return R.mipmap.rain;
                case 307:
                    return R.mipmap.heavyrain;
                case 308:
                    return R.mipmap.heavyrain;
                case 309:
                    return R.mipmap.lightrain;
                case 310:
                    return R.mipmap.heavyrain;
                case 311:
                    return R.mipmap.heavyrain;
                case 312:
                    return R.mipmap.heavyrain;
                case 313:
                    return R.mipmap.sleet;
                case 400:
                    return R.mipmap.lightsnow;
                case 401:
                    return R.mipmap.snow;
                case 402:
                    return R.mipmap.heavysnow;
                case 403:
                    return R.mipmap.heavysnow;
                case 404:
                    return R.mipmap.sleet;
                case 405:
                    return R.mipmap.sleet;
                case 406:
                    return R.mipmap.sleet;
                case 407:
                    return R.mipmap.sleet;
                case 500:
                    return R.mipmap.frog;
                case 501:
                    return R.mipmap.frog;
                case 502:
                    return R.mipmap.frog;
                case 503:
                    return R.mipmap.frog;
                default:
                    return R.mipmap.icon;
            }
        }else {
            return R.mipmap.icon;
        }

    }

    //定位当前位置
    public void GetGPSPlace() {
        //关闭侧滑栏
        if (weatherDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            weatherDrawerLayout.closeDrawer(GravityCompat.START);
        }
        if (null == locationOption) {
            locationOption = new AMapLocationClientOption();
        }
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        initLocation();
        startLocation();
    }

    /**
     * 开始定位
     *
     * @author
     * @since 2.8.0
     */
    private void startLocation() {
//        //根据控件的选择，重新设置定位参数
//        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
//                    sb.append("区域 码   : " + location.getAdCode() + "\n");
//                    sb.append("地    址    : " + location.getAddress() + "\n");
//                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
//                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                //定位之后的回调时间
//                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
                LogUtil.d(TAG, "定位result为==>：" + result);
//                Toast.makeText(WeatherActivity.this, TAG + "定位地址为" + result, Toast.LENGTH_SHORT).show();
                stopLocation();
                String provinceName = GetClearName(location.getProvince());
                String cityName = GetClearName(location.getCity());
                String countyName=GetClearName(location.getDistrict());


//                String GPSWeatherId = GetCountyWeatherId(provinceName,cityName,countyName);
//                RequestWeather(GPSWeatherId);
                GetCountyWeatherId(provinceName,cityName,countyName);
            } else {
                Toast.makeText(WeatherActivity.this, TAG + "定位失败", Toast.LENGTH_SHORT).show();
                stopLocation();
            }
        }
    };

    //获取当前city下的所有county
    public void GetCountyWeatherId(String provincename, String cityName,String countyName) {
        String weatherId = null;
        List<City> city = DataSupport.where("cityname=?", cityName).find(City.class);
        if (city.size()>0) {
            int cityid = city.get(0).getCityCode();
            countyList = DataSupport.where("cityid=?", String.valueOf(cityid)).find(County.class);
            if (countyList.size() > 0) {
                weatherId = countyList.get(0).getWeatherID();
                for (int i = 0; i < countyList.size(); i++) {
                    if (countyList.get(i).getCountyName().equals(cityName)) {
                        weatherId = countyList.get(i).getWeatherID();
                    }
                }
            }
            if (weatherId != null) {
                PutInfoToSP(this, SPWEATHERIDKEY, weatherId);
                RequestWeather(weatherId);
            }
        } else {
            // TODO 数据库中不存在时的处理

            List<Province> provinces=DataSupport.where("provincename=?", provincename).find(Province.class);
            int provinceId=provinces.get(0).getProvinceCode();
             queryFromServer(provinceId,cityName,countyName);
        }
    }

    //获取省市县数据
    public void queryFromServer(int provinceid,String cityname,String countyname) {
        // 构建做好相关配置的 OkHttpClient 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Params.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            cityFromServer(retrofit, provinceid,cityname,countyname);
    }



    //定位时如果数据库中不存在时取city信息
    public void cityFromServer(final Retrofit retrofit, final int provinceId, final String cityname,final String countyname) {
        ICityService CityService = retrofit.create(ICityService.class);
        Call<List<CityEntity>> call = CityService.getCity(provinceId);
        call.enqueue(new Callback<List<CityEntity>>() {
            @Override
            public void onResponse(Call<List<CityEntity>> call, retrofit2.Response<List<CityEntity>> response) {
                City city=Utility.GPSCityResponse(response.body(), provinceId,cityname);
                int cityid=city.getCityCode();
                //city下的counties
                countyFromServer(retrofit,provinceId,cityid,countyname);
            }

            @Override
            public void onFailure(Call<List<CityEntity>> call, Throwable t) {
                call.cancel();
            }
        });
    }
    //定位时如果数据库中不存在时取county信息
    public void countyFromServer(Retrofit retrofit, final int provinceId, final int cityId,final String countyname) {
        ICountyService CountyService = retrofit.create(ICountyService.class);
        Call<List<CountyEntity>> call = CountyService.getCounty(provinceId, cityId);
        call.enqueue(new Callback<List<CountyEntity>>() {
            @Override
            public void onResponse(Call<List<CountyEntity>> call, Response<List<CountyEntity>> response) {
                County county=GPSCountyResponse(response.body(),cityId,countyname);
                String weatherid=county.getWeatherID();
                PutInfoToSP(WeatherActivity.this, Params.SPWEATHERIDKEY, weatherid);
                RequestWeather(weatherid);
            }

            @Override
            public void onFailure(Call<List<CountyEntity>> call, Throwable t) {
                call.cancel();
            }
        });


    }


    private String GetClearName(String cityname) {
        String removeSheng = "省";
        String removeShi = "市";
        String removeQu = "区";
        String removeXian = "县";
        String cityName = null;

        cityName = cityname.replace(removeSheng, "");
        cityName = cityName.replace(removeShi, "");
        cityName = cityName.replace(removeQu, "");
        cityName = cityName.replace(removeXian, "");
        return cityName;
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }
}
