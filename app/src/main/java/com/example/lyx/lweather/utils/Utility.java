package com.example.lyx.lweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.lyx.lweather.dbase.City;
import com.example.lyx.lweather.dbase.County;
import com.example.lyx.lweather.dbase.Province;
import com.example.lyx.lweather.network.entity.CityEntity;
import com.example.lyx.lweather.network.entity.CountyEntity;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;
import com.example.lyx.lweather.network.entity.ProvinceEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/6/13.
 */

public class Utility {
    private static final String TAG="UtilLity==>";
    /*province数据处理*/
    public static boolean handleProvinceResponse(List<ProvinceEntity> response){
        if (!TextUtils.isEmpty(response.toString())) {
            LogUtil.d(TAG,"handleresponse"+response.toString());
            for (int i=0;i<response.size();i++){
                ProvinceEntity provinceEntity=response.get(i);
                Province province= new Province();
                province.setProvinceName(provinceEntity.getName());
                province.setProvinceCode(provinceEntity.getId());
                province.save();
            }
            return true;
        }
        return false;
    }


    /*city数据处理*/
    public static boolean handleCityResponse(List<CityEntity> response, int provinceId){
        if (!TextUtils.isEmpty(response.toString())) {
            for (int i=0;i<response.size();i++){
                City city=new City();
                CityEntity cityEntity=response.get(i);
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
    public static boolean handleCountyResponse(List<CountyEntity> response, int cityId){
        if (!TextUtils.isEmpty(response.toString())) {
                for (int i=0;i<response.size();i++){
                    County county=new County();
                    CountyEntity countyEntity=response.get(i);
                    LogUtil.i(TAG,"countyEntity==>"+countyEntity.toString());
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
     *
     * 存到SharedPreferences
     * @param context
     * @param Info
     */
    public static void PutInfoToSP(Context context,String prekey, String Info){
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(prekey,Info);
        editor.commit();
    }

    /**
     * 从SharedPreferences中取出
     * @param context
     * @return
     */
    public static String GetInfoFromSP(Context context,String prekey){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        String WeatherId=preferences.getString(prekey,null);
        return WeatherId;
    }

    /**
     *
     * 存到SharedPreferences
     * @param context
     * @param Info
     */
    public static void PutWeatherToSP(Context context,String prekey, CountyWeatherEntity Info){
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(prekey,Info.toString());
//        Boolean successed=editor.commit();
        editor.commit();
//        LogUtil.d(TAG,"存储==>"+successed);
    }




}
