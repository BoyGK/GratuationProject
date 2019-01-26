package com.gkpoter.graduationproject.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.adapter.WifiManagerAdapter;
import com.gkpoter.graduationproject.bean.DataClass;
import com.gkpoter.graduationproject.bean.PositionClass;
import com.gkpoter.graduationproject.utils.HttpRequest;
import com.gkpoter.graduationproject.utils.SharedPreferencesHelper;
import com.gkpoter.graduationproject.view.BGQPositioningView;
import com.gkpoter.graduationproject.view.BGQShapeHelper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PositioningActivity extends AppCompatActivity {

    private MapView mapView;
    private ArcGISMap mMap;
    private BGQShapeHelper shapeHelper;
    private BGQPositioningView bgqMap;

    private WifiManager wifi_manager;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        mapView = findViewById(R.id.positioning);
        bgqMap = findViewById(R.id.positioning_top);

        initMap();
        collection4position();
    }

    private void collection4position() {

        /**
         * wifi定位
         */
        SharedPreferencesHelper helper =
                new SharedPreferencesHelper("shpInfo", getApplicationContext());
        String mapId = helper.getData("name", "");
        handler = new Handler();
        runnable = new Runnable() {
            @SuppressLint("HardwareIds")
            @Override
            public void run() {
                wifi_manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                if (wifi_manager != null) {
                    wifi_manager.startScan();
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
                    System.out.println(new Gson().toJson(jsonObj));
                    params.add("wifiLoc", new Gson().toJson(jsonObj));
                    HttpRequest.post(getApplicationContext(), "/wifi", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String re = new String(responseBody);
                            PositionClass position = new Gson().fromJson(re, PositionClass.class);
                            if (position.getState() == 1) {
                                showLocation(position.getPoint().getX(), position.getPoint().getY());
                            }
                            handler.postDelayed(runnable, 3000);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            System.out.println("error");
                            handler.postDelayed(runnable, 3000);
                        }
                    });
                }
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void showLocation(double x, double y) {
        double lx = (shapeHelper.getMaxX() - shapeHelper.getMinX()) * x + shapeHelper.getMinX();
        double ly = (shapeHelper.getMaxY() - shapeHelper.getMinY()) * y + shapeHelper.getMinY();
        Point p = new Point(lx, ly, mapView.getSpatialReference());
        android.graphics.Point point = mapView.locationToScreen(p);
        bgqMap.clear();
        bgqMap.addPoint(point, 50);

        //mapView.setViewpointCenterAsync(new Point(lx, ly));

        mapView.addViewpointChangedListener(new ViewpointChangedListener() {
            @Override
            public void viewpointChanged(ViewpointChangedEvent viewpointChangedEvent) {
                Point p = new Point(lx, ly, mapView.getSpatialReference());
                android.graphics.Point point = mapView.locationToScreen(p);
                bgqMap.clear();
                bgqMap.addPoint(point, 50);
            }
        });
    }

    private void initMap() {
        mMap = new ArcGISMap(new Basemap());
        mapView.setMap(mMap);

        String path = new SharedPreferencesHelper("shpPath", getApplicationContext())
                .getData("shp", "");
        if ("".equals(path)) {
            Toast.makeText(getApplicationContext(), "请先导入地图。", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            shapeHelper = new BGQShapeHelper(path.replaceAll("file:/", ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidShapeFileException e) {
            e.printStackTrace();
        }
        symbolizeShapefile(path.replaceAll("file:/", ""));
    }

    private void symbolizeShapefile(String path) {

        // create a shapefile feature table from the local data
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);

        // use the shapefile feature table to create a feature layer
        FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);

        // create the Symbol
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);

        // create the Renderer
        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);

        // set the Renderer on the Layer
        featureLayer.setRenderer(renderer);

        // add the feature layer to the map
        mMap.getOperationalLayers().add(featureLayer);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
