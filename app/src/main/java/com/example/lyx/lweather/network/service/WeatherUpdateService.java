package com.example.lyx.lweather.network.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.lyx.lweather.R;
import com.example.lyx.lweather.activity.MainActivity;
import com.example.lyx.lweather.activity.WeatherActivity;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;
import com.example.lyx.lweather.utils.LogUtil;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.lyx.lweather.utils.Params.BASE_URL;
import static com.example.lyx.lweather.utils.Params.WEATHERPROKEY;
import static com.example.lyx.lweather.utils.Utility.GetInfoFromSP;
import static com.example.lyx.lweather.utils.Utility.SaveByFastJson;
import static com.example.lyx.lweather.utils.Utility.getBeanByFastJson;

public class WeatherUpdateService extends Service {
    public static final String TAG = "WeatherUpdateService";
    public static final String ACTION_UPDATE = "update";
    RemoteViews remoteViews;//自定义notification
    Notification notification;
    UpdateReceiver updateReceiver;

    public WeatherUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //准备广播接收器
        PrepareReceiver();
        //定时好启动service
        UpdateTime();
        UpdateSPWeather();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //事件设定
    public void UpdateTime() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 60 * 60 * 1000 * 6;//设定为6个小时
        Intent i = new Intent(this, UpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }


    //更新weather并传到sharedprefrences
    public void UpdateSPWeather() {
        String weatherid = GetInfoFromSP(this, "weatherid");
        RequestWeather(weatherid);

//        CountyWeatherEntity weatherEntity = getBeanByFastJson(getApplicationContext(), "weather", CountyWeatherEntity.class);
//        ShowNotification(weatherEntity);

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
                //取到的数据直接存到sharedPreference
                Boolean isSaved = SaveByFastJson(getApplicationContext(), "weather", response.body());
                ShowNotification(response.body());
            }

            @Override
            public void onFailure(Call<CountyWeatherEntity> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "天气信息加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //准备广播接收器
    public void PrepareReceiver() {
        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE);
        registerReceiver(updateReceiver, intentFilter);
    }


    //将数据展示到notification
    public void ShowNotification(CountyWeatherEntity weatherEntity) {

        String cityName = weatherEntity.getHeWeather().get(0).getBasic().getCity();
        String temp = weatherEntity.getHeWeather().get(0).getNow().getTmp();
        String cityWeather = weatherEntity.getHeWeather().get(0).getNow().getCond().getTxt();
        String weatherCode = weatherEntity.getHeWeather().get(0).getNow().getCond().getCode();
        String updateTime = weatherEntity.getHeWeather().get(0).getBasic().getUpdate().getUtc();


        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext())
                .setContent(remoteViews);
        builder.setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(getIcon(weatherCode));


        remoteViews.setTextViewText(R.id.cityname_noti,cityName);
        remoteViews.setTextViewText(R.id.nowtemp_noti,temp+"℃");
        remoteViews.setTextViewText(R.id.weatherword_noti,cityWeather);
        remoteViews.setTextViewText(R.id.updatetime_noti,updateTime);
        remoteViews.setImageViewResource(R.id.weathericon_noti,getIcon(weatherCode));


        //点击notification跳转程序
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);


        notification = builder.getNotification();
        startForeground(1, notification);
    }

    //根据weathercode选择天气图标
    public int getIcon(String weathercode) {
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
    }


    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context, WeatherUpdateService.class);
            context.startService(i);

//            String action = intent.getAction();
//            if (action.equals(ACTION_UPDATE)) {
//                UpdateSPWeather();
//            }
        }
    }

}

/*
* 代码	中文	英文	ICON
100	晴	Sunny/Clear	https://cdn.heweather.com/cond_icon/100.png
101	多云	Cloudy	https://cdn.heweather.com/cond_icon/101.png
102	少云	Few Clouds	https://cdn.heweather.com/cond_icon/102.png
103	晴间多云	Partly Cloudy	https://cdn.heweather.com/cond_icon/103.png
104	阴	Overcast	https://cdn.heweather.com/cond_icon/104.png
200	有风	Windy	https://cdn.heweather.com/cond_icon/200.png
201	平静	Calm	https://cdn.heweather.com/cond_icon/201.png
202	微风	Light Breeze	https://cdn.heweather.com/cond_icon/202.png
203	和风	Moderate/Gentle Breeze	https://cdn.heweather.com/cond_icon/203.png
204	清风	Fresh Breeze	https://cdn.heweather.com/cond_icon/204.png
205	强风/劲风	Strong Breeze	https://cdn.heweather.com/cond_icon/205.png
206	疾风	High Wind, Near Gale	https://cdn.heweather.com/cond_icon/206.png
207	大风	Gale	https://cdn.heweather.com/cond_icon/207.png
208	烈风	Strong Gale	https://cdn.heweather.com/cond_icon/208.png
209	风暴	Storm	https://cdn.heweather.com/cond_icon/209.png
210	狂爆风	Violent Storm	https://cdn.heweather.com/cond_icon/210.png
211	飓风	Hurricane	https://cdn.heweather.com/cond_icon/211.png
212	龙卷风	Tornado	https://cdn.heweather.com/cond_icon/212.png
213	热带风暴	Tropical Storm	https://cdn.heweather.com/cond_icon/213.png
300	阵雨	Shower Rain	https://cdn.heweather.com/cond_icon/300.png
301	强阵雨	Heavy Shower Rain	https://cdn.heweather.com/cond_icon/301.png
302	雷阵雨	Thundershower	https://cdn.heweather.com/cond_icon/302.png
303	强雷阵雨	Heavy Thunderstorm	https://cdn.heweather.com/cond_icon/303.png
304	雷阵雨伴有冰雹	Hail	https://cdn.heweather.com/cond_icon/304.png
305	小雨	Light Rain	https://cdn.heweather.com/cond_icon/305.png
306	中雨	Moderate Rain	https://cdn.heweather.com/cond_icon/306.png
307	大雨	Heavy Rain	https://cdn.heweather.com/cond_icon/307.png
308	极端降雨	Extreme Rain	https://cdn.heweather.com/cond_icon/308.png
309	毛毛雨/细雨	Drizzle Rain	https://cdn.heweather.com/cond_icon/309.png
310	暴雨	Storm	https://cdn.heweather.com/cond_icon/310.png
311	大暴雨	Heavy Storm	https://cdn.heweather.com/cond_icon/311.png
312	特大暴雨	Severe Storm	https://cdn.heweather.com/cond_icon/312.png
313	冻雨	Freezing Rain	https://cdn.heweather.com/cond_icon/313.png
400	小雪	Light Snow	https://cdn.heweather.com/cond_icon/400.png
401	中雪	Moderate Snow	https://cdn.heweather.com/cond_icon/401.png
402	大雪	Heavy Snow	https://cdn.heweather.com/cond_icon/402.png
403	暴雪	Snowstorm	https://cdn.heweather.com/cond_icon/403.png
404	雨夹雪	Sleet	https://cdn.heweather.com/cond_icon/404.png
405	雨雪天气	Rain And Snow	https://cdn.heweather.com/cond_icon/405.png
406	阵雨夹雪	Shower Snow	https://cdn.heweather.com/cond_icon/406.png
407	阵雪	Snow Flurry	https://cdn.heweather.com/cond_icon/407.png
500	薄雾	Mist	https://cdn.heweather.com/cond_icon/500.png
501	雾	Foggy	https://cdn.heweather.com/cond_icon/501.png
502	霾	Haze	https://cdn.heweather.com/cond_icon/502.png
503	扬沙	Sand	https://cdn.heweather.com/cond_icon/503.png
504	浮尘	Dust	https://cdn.heweather.com/cond_icon/504.png
507	沙尘暴	Duststorm	https://cdn.heweather.com/cond_icon/507.png
508	强沙尘暴	Sandstorm	https://cdn.heweather.com/cond_icon/508.png
900	热	Hot	https://cdn.heweather.com/cond_icon/900.png
901	冷	Cold	https://cdn.heweather.com/cond_icon/901.png
999	未知	Unknown	https://cdn.heweather.com/cond_icon/999.png
*
*
* */