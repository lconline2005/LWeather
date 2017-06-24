package com.example.lyx.lweather.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lyx.lweather.R;
import com.example.lyx.lweather.fragment.ChooseAreaFragment;

import static com.example.lyx.lweather.utils.Utility.GetInfoFromSP;

public class MainActivity extends BaseActivity {
    Fragment fragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setColor(this,getResources().getColor(R.color.toolbarbackground));
        if (GetInfoFromSP(this,"weather")!=null) {
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            manager = getSupportFragmentManager();
            fragment= new ChooseAreaFragment();
            transaction = manager.beginTransaction();
            transaction.add(R.id.choose_area_fragment, fragment);
            transaction.commit();
        }
    }
}
