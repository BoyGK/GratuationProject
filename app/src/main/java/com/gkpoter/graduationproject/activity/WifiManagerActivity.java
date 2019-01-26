package com.gkpoter.graduationproject.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.adapter.WifiManagerAdapter;
import com.gkpoter.graduationproject.bean.DataClass;
import com.gkpoter.graduationproject.bean.PositionClass;
import com.gkpoter.graduationproject.utils.HttpRequest;
import com.gkpoter.graduationproject.utils.SharedPreferencesHelper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WifiManagerActivity extends AppCompatActivity {

    private ListView listView;
    private WifiManagerAdapter adapter;
    private WifiManager wifi_manager;

    private Button start, end;
    private Runnable runnable;
    private Handler handler;

    private String mapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_manager);
        start = findViewById(R.id.w_s);
        end = findViewById(R.id.w_e);

        String[] PERMS_INITIAL = {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(PERMS_INITIAL, 127);

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);

        SharedPreferencesHelper helper =
                new SharedPreferencesHelper("shpInfo", getApplicationContext());
        mapId = helper.getData("name", "");

        listView = findViewById(R.id.wifi_list);
        wifi_manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifi_manager != null) {
            System.out.println(wifi_manager.startScan());
        }

        handler = new Handler();
        runnable = new Runnable() {
            @SuppressLint("HardwareIds")
            @Override
            public void run() {
                wifi_manager.startScan();
                handler.postDelayed(this, 1000);
            }
        };
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(runnable, 1000);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (adapter == null) {
                    adapter = new WifiManagerAdapter(wifi_manager.getScanResults(), WifiManagerActivity.this);
                    listView.setAdapter(adapter);
                } else {
                    adapter.setScanResults(wifi_manager.getScanResults());
                    adapter.notifyDataSetChanged();
                }

                List<ScanResult> results = wifi_manager.getScanResults();
                DataClass jsonObj = new DataClass();
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                if (wifi != null) {
                    WifiInfo info = wifi.getConnectionInfo();
                    jsonObj.setMAC(info.getMacAddress() + "");
                }
                List<DataClass.APsBean> aPsBeens = new ArrayList<>();
                for (ScanResult result : results) {
                    DataClass.APsBean aPsBean = new DataClass.APsBean();
                    aPsBean.setMAC(result.BSSID);
                    aPsBean.setFrequency(result.frequency);
                    aPsBean.setName(result.SSID);
                    aPsBean.setRSSI(result.level);
                    aPsBeens.add(aPsBean);
                }
                jsonObj.setAPs(aPsBeens);
                jsonObj.setMapId(mapId);
                RequestParams params = new RequestParams();
                params.add("wifi", new Gson().toJson(jsonObj));
                HttpRequest.post(getApplicationContext(), "/api/android_data", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println("success");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        System.out.println("error");
                    }
                });

            }
        }
    };
}
