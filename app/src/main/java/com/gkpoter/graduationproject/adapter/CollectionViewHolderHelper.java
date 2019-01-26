package com.gkpoter.graduationproject.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.activity.ActivityContext;
import com.gkpoter.graduationproject.activity.WifiManagerActivity;
import com.gkpoter.graduationproject.bean.DataClass;
import com.gkpoter.graduationproject.utils.FileHelper;
import com.gkpoter.graduationproject.utils.SharedPreferencesHelper;
import com.gkpoter.graduationproject.view.BGQMapView;
import com.gkpoter.graduationproject.view.BGQShapeHelper;
import com.google.gson.Gson;

import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public class CollectionViewHolderHelper extends BaseAdapterHelper
        implements View.OnClickListener, SensorEventListener {

    private FloatingActionButton collectionKey;
    private static boolean COL_KEY = true;
    private FloatingActionButton layerKey;

    private MapView arcgisMap;
    private ArcGISMap mMap;
    private BGQMapView bgqMap;
    private BGQShapeHelper shapeHelper;
    private double percentageX, percentageY;

    private LinearLayout titleEdit;
    private LinearLayout titleView;

    private Button collectionStart;
    private Button collectionEnd;

    private int stepCount;
    private int detectorCount = 0;
    private EditText editCount;
    private TextView mStepCount;
    private TextView mDetectorCount;
    private SensorManager mSensorManager;
    private Sensor mStepDetector;

    private WifiManager wifi_manager;
    private Runnable runnable;
    private Handler handler;
    private List<DataClass> jsonData = new ArrayList<>();

    public CollectionViewHolderHelper(ActivityContext context) {
        super(context);
    }

    @Override
    public void initView(View itemView) {
        collectionKey = itemView.findViewById(R.id.collectionkey);
        layerKey = itemView.findViewById(R.id.layerkey);
        arcgisMap = itemView.findViewById(R.id.arcgismap);
        bgqMap = itemView.findViewById(R.id.bgqmap);
        collectionStart = itemView.findViewById(R.id.collectionstart);
        collectionEnd = itemView.findViewById(R.id.collectionend);
        titleEdit = itemView.findViewById(R.id.titleLayout_edit);
        titleView = itemView.findViewById(R.id.titleLayout_view);
        editCount = itemView.findViewById(R.id.editcount);
        mStepCount = itemView.findViewById(R.id.viewstep);
        mDetectorCount = itemView.findViewById(R.id.viewdetector);

        collectionKey.setOnClickListener(this);
        layerKey.setOnClickListener(this);
        collectionStart.setOnClickListener(this);
        collectionEnd.setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void doBusiness() {
        initMap();
        /**
         * 计步
         */
        mSensorManager = (SensorManager) getActivityContext().getActivity().getSystemService(SENSOR_SERVICE);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        /**
         * 采集位置获取，已转换为百分比
         */
        bgqMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println(123456);
                Point clickPoint = arcgisMap.screenToLocation(
                        new android.graphics.Point(Math.round(motionEvent.getX()), Math.round(motionEvent.getY())));
                percentageX = (clickPoint.getX() - shapeHelper.getMinX())
                        / (shapeHelper.getMaxX() - shapeHelper.getMinX());
                percentageY = (clickPoint.getY() - shapeHelper.getMinY())
                        / (shapeHelper.getMaxY() - shapeHelper.getMinY());
                return false;
            }
        });

        /**
         * wifi采集
         */
        SharedPreferencesHelper helper =
                new SharedPreferencesHelper("shpInfo", getActivityContext().getContext());
        String mapId = helper.getData("name", "");
        handler = new Handler();
        runnable = new Runnable() {
            @SuppressLint("HardwareIds")
            @Override
            public void run() {
                wifi_manager = (WifiManager) getActivityContext().getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
                if (wifi_manager != null) {
                    wifi_manager.startScan();
                    List<ScanResult> results = wifi_manager.getScanResults();
                    DataClass jsonObj = new DataClass();
                    WifiManager wifi = (WifiManager) getActivityContext().getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
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
                    jsonObj.setPoint(new DataClass.MyPoint(percentageX, percentageY));
                    jsonData.add(jsonObj);
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (sensorEvent.values[0] == 1.0) {
                detectorCount++;
                mDetectorCount.setText("行走步数:" + detectorCount);
                if (detectorCount >= stepCount) {
                    /**
                     * 注销wifi采集
                     */
                    handler.removeCallbacks(runnable);
                    FileHelper fileHelper = new FileHelper();
                    try {
                        String json = new Gson().toJson(jsonData);
                        fileHelper.save("wifi.txt", json.substring(0, json.length() - 1));
                        jsonData = new ArrayList<>();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    titleEdit.setVisibility(View.VISIBLE);
                    titleView.setVisibility(View.GONE);
                    detectorCount = 0;
                    mSensorManager.unregisterListener(this, mStepDetector);
                    mDetectorCount.setText("行走步数:0");
                    Toast.makeText(getActivityContext().getContext(), "采集完成", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layerkey:
                if (bgqMap.getVisibility() == View.GONE) {
                    layerKey.setImageResource(R.drawable.ic_layers_clear_black_24dp);
                    bgqMap.setVisibility(View.VISIBLE);
                } else {
                    layerKey.setImageResource(R.drawable.ic_layers_black_24dp);
                    bgqMap.setVisibility(View.GONE);
                }
                break;
            case R.id.collectionkey:
                ((RecyclerView) getActivityContext().getView()).getAdapter().notifyDataSetChanged();
                if (!COL_KEY) {
                    collectionKey.setImageResource(R.drawable.ic_accessibility_black_24dp);
                    COL_KEY = true;
                    bgqMap.changeCollectionWay(BGQMapView.COLLECTION_STOP);
                } else {
                    collectionKey.setImageResource(R.drawable.ic_directions_walk_black_24dp);
                    COL_KEY = false;
                    bgqMap.changeCollectionWay(BGQMapView.COLLECTION_WALK);
                }
                break;
            case R.id.collectionstart:
                if (bgqMap.getVisibility() == View.GONE) {
                    Toast.makeText(getActivityContext().getContext(), "请先锁定图层选择位置！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!"".equals(editCount.getText().toString())) {
                    titleEdit.setVisibility(View.GONE);
                    titleView.setVisibility(View.VISIBLE);
                    mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
                    stepCount = Integer.parseInt(editCount.getText().toString());
                    editCount.setText("");
                    mStepCount.setText("预计步数:" + stepCount);
                    /**
                     * 开启wifi采集
                     */
                    handler.postDelayed(runnable, 1000);
                }
                break;
            case R.id.collectionend:
                titleEdit.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.GONE);
                detectorCount = 0;
                mSensorManager.unregisterListener(this, mStepDetector);
                mDetectorCount.setText("行走步数:0");
                /**
                 * 注销wifi采集
                 */
                handler.removeCallbacks(runnable);
                FileHelper fileHelper = new FileHelper();
                try {
                    String json = new Gson().toJson(jsonData);
                    fileHelper.save("wifi.txt", json.substring(0, json.length() - 1));
                    jsonData = new ArrayList<>();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initMap() {
        mMap = new ArcGISMap(new Basemap());
        arcgisMap.setMap(mMap);

        String path = new SharedPreferencesHelper("shpPath", getActivityContext().getContext())
                .getData("shp", "");
        if ("".equals(path)) {
            Toast.makeText(getActivityContext().getContext(), "请先导入地图。", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            shapeHelper = new BGQShapeHelper(path);
            System.out.println(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidShapeFileException e) {
            e.printStackTrace();
        }
        symbolizeShapefile(path);
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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

            }
        }
    };
}
