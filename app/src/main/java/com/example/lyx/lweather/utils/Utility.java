package com.example.lyx.lweather.utils;

import android.text.TextUtils;

import com.example.lyx.lweather.dbase.City;
import com.example.lyx.lweather.dbase.County;
import com.example.lyx.lweather.dbase.Province;
import com.example.lyx.lweather.network.entity.CityEntity;
import com.example.lyx.lweather.network.entity.CountyEntity;
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
//    /*province数据处理*/
//    public static boolean handleProvinceResponse(String response){
//        if (!TextUtils.isEmpty(response)) {
//            try {
//                JSONArray allProvinces=new JSONArray(response);
//                for (int i=0;i<allProvinces.length();i++){
//                    JSONObject provinceObject = allProvinces.getJSONObject(i);
//                    Province province=new Province();
//                    province.setProvinceName(provinceObject.getString("name"));
//                    province.setProvinceCode(provinceObject.getInt("id"));
//                    province.save();
//                }
//                return true;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }


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




//    /*city数据处理*/
//    public static boolean handleCityResponse(String response,int provinceId){
//        if (!TextUtils.isEmpty(response)) {
//            try {
//                JSONArray allCities=new JSONArray(response);
//                for (int i=0;i<allCities.length();i++){
//                    JSONObject cityObject = allCities.getJSONObject(i);
//                    City city=new City();
//                    city.setCityName(cityObject.getString("name"));
//                    city.setCityCode(cityObject.getInt("id"));
//                    city.setProvinceID(provinceId);
//                    city.save();
//                }
//                return true;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }


    /*county数据处理*/
    public static boolean handleCountyResponse(List<CountyEntity> response, int cityId){
        if (!TextUtils.isEmpty(response.toString())) {
                for (int i=0;i<response.size();i++){
                    County county=new County();
                    CountyEntity countyEntity=response.get(i);
                    county.setCityID(cityId);
                    county.setCountyName(countyEntity.getName());
                    county.setWeatherID(countyEntity.getWeather_id());
                    county.save();
                }
                return true;
        }
        return false;
    }




//    /*county数据处理*/
//    public static boolean handleCountyResponse(String response,int cityId){
//        if (!TextUtils.isEmpty(response)) {
//            try {
//                JSONArray allCounties=new JSONArray(response);
//                for (int i=0;i<allCounties.length();i++){
//                    JSONObject countyObject = allCounties.getJSONObject(i);
//                    County county=new County();
//                    county.setCountyName(countyObject.getString("name"));
//                    county.setWeatherID(countyObject.getString("weather_id"));
//                    county.setCityID(cityId);
//                    county.save();
//                }
//                return true;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
}
