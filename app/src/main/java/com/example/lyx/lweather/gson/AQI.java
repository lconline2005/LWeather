package com.example.lyx.lweather.gson;

/**
 * Created by Administrator on 2017/6/16.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
