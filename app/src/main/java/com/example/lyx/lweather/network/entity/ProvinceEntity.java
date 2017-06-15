package com.example.lyx.lweather.network.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/13.
 */

public class ProvinceEntity {

    /**
     * id : 1
     * name : 北京
     */

    private int id;
    private String name;

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
}
