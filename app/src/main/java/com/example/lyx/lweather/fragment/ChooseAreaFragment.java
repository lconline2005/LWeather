package com.example.lyx.lweather.fragment;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyx.lweather.R;
import com.example.lyx.lweather.dbase.City;
import com.example.lyx.lweather.dbase.County;
import com.example.lyx.lweather.dbase.Province;
import com.example.lyx.lweather.network.entity.CityEntity;
import com.example.lyx.lweather.network.entity.CountyEntity;
import com.example.lyx.lweather.network.entity.ProvinceEntity;
import com.example.lyx.lweather.network.service.ICityService;
import com.example.lyx.lweather.network.service.ICountyService;
import com.example.lyx.lweather.network.service.IProvinceService;
import com.example.lyx.lweather.utils.LogUtil;
import com.example.lyx.lweather.utils.Params;
import com.example.lyx.lweather.utils.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2017/6/13.
 */

public class ChooseAreaFragment extends Fragment {

    private String TAG = "LWeather_choosearea";
    public final static int LEVEL_PROVINCE = 0;
    public final static int LEVEL_CITY = 1;
    public final static int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titletext;
    private ImageView backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /*province列表*/
    private List<Province> provinceList;

    /*city列表*/
    private List<City> cityList;

    /*county列表*/
    private List<County> countyList;

    /*选中的province*/
    private Province selectedProvince;

    /*选中的city*/
    private City selectedCity;

    /*选中的county*/
    private County selectedCounty;

    /*当前选中的层级*/
    private int currentLevel;
    View view;
    Boolean isLoadSuccess = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area, container, false);
        titletext = (TextView) view.findViewById(R.id.title_text);
        backButton = (ImageView) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                    LogUtil.d(TAG, "==>" + selectedCity.getCityName() + selectedCity.getCityCode() + currentLevel);
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
    }

    //获取省市县数据
    public void queryFromServer(final String type) {
        isLoadSuccess = false;
        showProgressDialog();
        // 构建做好相关配置的 OkHttpClient 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Params.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if (type.equals("province")) {
            provinceFromServer(retrofit, type);
            LogUtil.d(TAG, "SecondisLoadSuccess==>" + isLoadSuccess.toString());
        } else if (type.equals("city")) {

            cityFromServer(retrofit, selectedProvince.getProvinceCode(), type);
        } else if (type.equals("county")) {
            LogUtil.d(TAG, "selectedProvince==>" + selectedProvince.getProvinceCode() + "==" + selectedCity.getCityCode());
            countyFromServer(retrofit, selectedProvince.getProvinceCode(), selectedCity.getCityCode(), type);
        }
    }

    public void IsLoad(final String type, Boolean isSuccessed) {
        if (isSuccessed) {
            closeProgressDialog();
            if (type.equals("province")) {
                queryProvinces();
            } else if (type.equals("city")) {
                queryCities();
            } else if (type.equals("county")) {
                queryCounties();
            }
        }
    }

    public void provinceFromServer(Retrofit retrofit, final String type) {
        IProvinceService ProvinceService = retrofit.create(IProvinceService.class);
        Call<List<ProvinceEntity>> call = ProvinceService.getProvince();
        call.enqueue(new Callback<List<ProvinceEntity>>() {
            @Override
            public void onResponse(Call<List<ProvinceEntity>> call, Response<List<ProvinceEntity>> response) {
                LogUtil.d(TAG, "resultProvince==>" + response.toString());
                isLoadSuccess = Utility.handleProvinceResponse(response.body());
                IsLoad(type, isLoadSuccess);
            }

            @Override
            public void onFailure(Call<List<ProvinceEntity>> call, Throwable t) {
                call.cancel();
                LoadFailed();
            }
        });
    }

    public void cityFromServer(Retrofit retrofit, final int provinceId, final String type) {
        ICityService CityService = retrofit.create(ICityService.class);
        Call<List<CityEntity>> call = CityService.getCity(provinceId);
        call.enqueue(new Callback<List<CityEntity>>() {
            @Override
            public void onResponse(Call<List<CityEntity>> call, retrofit2.Response<List<CityEntity>> response) {
                isLoadSuccess = Utility.handleCityResponse(response.body(), provinceId);
                LogUtil.d(TAG, "resultCity==>" + response.body());
                IsLoad(type, isLoadSuccess);
            }

            @Override
            public void onFailure(Call<List<CityEntity>> call, Throwable t) {
                call.cancel();
                LoadFailed();
            }
        });
    }

    public void countyFromServer(Retrofit retrofit, final int provinceId, final int cityId, final String type) {
        ICountyService CountyService = retrofit.create(ICountyService.class);
        Call<List<CountyEntity>> call = CountyService.getCounty(provinceId, cityId);
        call.enqueue(new Callback<List<CountyEntity>>() {
            @Override
            public void onResponse(Call<List<CountyEntity>> call, Response<List<CountyEntity>> response) {
                isLoadSuccess = Utility.handleCountyResponse(response.body(), cityId);
                LogUtil.d(TAG, "resultCounty==>" + response.body());
                IsLoad(type, isLoadSuccess);
            }

            @Override
            public void onFailure(Call<List<CountyEntity>> call, Throwable t) {
                call.cancel();
                LoadFailed();
            }
        });


    }


    //加载失败处理
    public void LoadFailed() {
        //通过runOnUiThread返回到主线程
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeProgressDialog();
                Toast.makeText(view.getContext(), "网络异常加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*查询所有province*/
    private void queryProvinces() {
        titletext.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (!provinceList.isEmpty() && provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer("province");
        }
    }


    /*查询province所有city*/
    private void queryCities() {
        titletext.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;

        } else {
            queryFromServer("city");
        }
    }

    /*查询city所有county*/
    private void queryCounties() {
        titletext.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
//        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer("county");
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
