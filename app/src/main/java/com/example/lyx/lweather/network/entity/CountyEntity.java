package com.example.lyx.lweather.network.entity;

/**
 * Created by Administrator on 2017/6/14.
 */

public class CountyEntity {
    /**
     * id : 109
     * name : 齐齐哈尔
     * weather_id : CN101050201
     */

    private int id;
    private String name;
    private String weather_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }
}
