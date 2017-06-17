package com.example.lyx.lweather.activity;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.example.lyx.lweather.R;
import com.example.lyx.lweather.fragment.ChooseAreaFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setColor(this,getResources().getColor(R.color.toolbarbackground));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather_id", null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            ChooseAreaFragment fragment = new ChooseAreaFragment();
            transaction.replace(R.id.choose_area_fragment_main, fragment);
            transaction.commit();

        }
    }
}
