package com.example.lyx.lweather.adapter;

import android.content.Context;

import com.example.lyx.lweather.CommonAdapter.RecyclerViewCommonAdapter;
import com.example.lyx.lweather.CommonAdapter.ViewHolder;
import com.example.lyx.lweather.R;
import com.example.lyx.lweather.network.entity.CountyWeatherEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator
 * on 2017/6/23.
 * 修改备注：
 */

public class DailyRecyclerAdapter extends RecyclerViewCommonAdapter<CountyWeatherEntity.HeWeatherBean.DailyForecastBean> {

    public DailyRecyclerAdapter(Context context, List<CountyWeatherEntity.HeWeatherBean.DailyForecastBean> Data, int layoutId) {
        super(context, Data, layoutId);
    }

    @Override
    protected void convert(ViewHolder holder, CountyWeatherEntity.HeWeatherBean.DailyForecastBean itemData, int position) {
//        holder.setText(R.id.datetext_recycler,itemData.getDate());
//        holder.setText(R.id.datetext_recycler,getMonths(itemData.getDate()));
        if (itemData.getCond().getTxt_d().equals(itemData.getCond().getTxt_n())) {
            holder.setText(R.id.condtext_recycler,itemData.getCond().getTxt_d());
        } else {
            holder.setText(R.id.condtext_recycler,itemData.getCond().getTxt_d()+"转"+itemData.getCond().getTxt_n());
        }

        holder.setText(R.id.maxtemp_recycler,itemData.getTmp().getMax()+"℃");
        holder.setText(R.id.mintemp_recycler,itemData.getTmp().getMin()+"℃");
    }
    public String getMonths(String dateYear){
        Date date=null;
        DateFormat sdf = new SimpleDateFormat("MM-dd");
        try {
            date=sdf.parse(dateYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.toString();
    }
}
