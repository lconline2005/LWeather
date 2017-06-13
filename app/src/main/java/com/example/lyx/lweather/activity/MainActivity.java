package com.example.lyx.lweather.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lyx.lweather.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setColor(this,getResources().getColor(R.color.toolbarbackground));
    }
}
