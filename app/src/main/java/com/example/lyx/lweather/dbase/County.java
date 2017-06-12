package com.example.lyx.lweather.dbase;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/13.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherID;
    private int cityID;

    @Override
    public String toString() {
        return "County{" +
                "id=" + id +
                ", countyName='" + countyName + '\'' +
                ", weatherID='" + weatherID + '\'' +
                ", cityID=" + cityID +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
