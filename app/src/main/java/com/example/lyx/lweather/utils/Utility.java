package com.example.lyx.lweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.example.lyx.lweather.R;
import com.example.lyx.lweather.dbase.City;
import com.example.lyx.lweather.dbase.County;
import com.example.lyx.lweather.dbase.Province;
import com.example.lyx.lweather.network.entity.CityEntity;
import com.example.lyx.lweather.network.entity.CountyEntity;
import com.example.lyx.lweather.network.entity.ProvinceEntity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Administrator on 2017/6/13.
 */

public class Utility {
    private static final String TAG = "UtilLity==>";

    /*province数据处理*/
    public static boolean handleProvinceResponse(List<ProvinceEntity> response) {
        if (!TextUtils.isEmpty(response.toString())) {
            LogUtil.d(TAG, "handleresponse" + response.toString());
            for (int i = 0; i < response.size(); i++) {
                ProvinceEntity provinceEntity = response.get(i);
                Province province = new Province();
                province.setProvinceName(provinceEntity.getName());
                province.setProvinceCode(provinceEntity.getId());
                province.save();
            }
            return true;
        }
        return false;
    }

    /*city数据处理*/
    public static boolean handleCityResponse(List<CityEntity> response, int provinceId) {
        if (!TextUtils.isEmpty(response.toString())) {
            for (int i = 0; i < response.size(); i++) {
                City city = new City();
                CityEntity cityEntity = response.get(i);
                city.setProvinceID(provinceId);
                city.setCityName(cityEntity.getName());
                city.setCityCode(cityEntity.getId());
                city.save();
            }
            return true;
        }
        return false;
    }


    /*county数据处理*/
    public static boolean handleCountyResponse(List<CountyEntity> response, int cityId) {
        if (!TextUtils.isEmpty(response.toString())) {
            for (int i = 0; i < response.size(); i++) {
                County county = new County();
                CountyEntity countyEntity = response.get(i);
                LogUtil.i(TAG, "countyEntity==>" + countyEntity.toString());
                county.setCityID(cityId);
                county.setCountyName(countyEntity.getName());
                county.setWeatherID(countyEntity.getWeather_id());
                county.save();
            }
            return true;
        }
        return false;
    }


    /**
     * 存到SharedPreferences
     *
     * @param context
     * @param Info
     */
    public static void PutInfoToSP(Context context, String prekey, String Info) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putString(prekey, Info);
        editor.commit();
    }

    /**
     * 从SharedPreferences中取出
     *
     * @param context
     * @return
     */
    public static String GetInfoFromSP(Context context, String prekey) {
        SharedPreferences preferences = getDefaultSharedPreferences(context);
        String WeatherId = preferences.getString(prekey, null);
        return WeatherId;
    }

    public static boolean SaveByFastJson(Context context, String key, Object obj) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        String objString = JSON.toJSONString(obj);
        return editor.putString(key, objString).commit();
    }

    /**
     * @param context
     * @param key
     * @param clazz   这里传入一个类就是我们所需要的实体类(obj)
     * @return 返回我们封装好的该实体类(obj)
     */
    public static <T> T getBeanByFastJson(Context context, String key,
                                          Class<T> clazz) {
        String objString = getDefaultSharedPreferences(context).getString(key, "");
        return JSON.parseObject(objString, clazz);
    }




    /*定位city数据处理*/
    public static City GPSCityResponse(List<CityEntity> response, int provinceId,String cityname) {
        City selectedcity=null;
        if (!TextUtils.isEmpty(response.toString())) {
            for (int i = 0; i < response.size(); i++) {
                City city = new City();
                CityEntity cityEntity = response.get(i);
                city.setProvinceID(provinceId);
                city.setCityName(cityEntity.getName());
                city.setCityCode(cityEntity.getId());
                city.save();
                if (i==0) {
                    selectedcity=city;
                } else if (city.getCityName().equals(cityname)) {
                    selectedcity=city;
                }
            }
            return selectedcity;
        }
        return selectedcity;
    }
    /*定位county数据处理*/
    public static County GPSCountyResponse(List<CountyEntity> response, int cityId,String countyname) {
        County selectedcounty=null;
        if (!TextUtils.isEmpty(response.toString())) {
            for (int i = 0; i < response.size(); i++) {
                County county = new County();
                CountyEntity countyEntity = response.get(i);
                LogUtil.i(TAG, "countyEntity==>" + countyEntity.toString());
                county.setCityID(cityId);
                county.setCountyName(countyEntity.getName());
                county.setWeatherID(countyEntity.getWeather_id());
                county.save();
                if (i==0) {
                    selectedcounty=county;
                } else if (county.getCountyName().equals(countyname)) {
                    selectedcounty=county;
                }
            }
            return selectedcounty;
        }
        return selectedcounty;
    }




    //根据weathercode选择天气图标
    public static int getIcon(String weathercode) {
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
}
