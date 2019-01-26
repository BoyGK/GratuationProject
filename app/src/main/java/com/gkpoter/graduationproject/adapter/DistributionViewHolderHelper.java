package com.gkpoter.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.activity.ActivityContext;
import com.gkpoter.graduationproject.utils.SharedPreferencesHelper;
import com.gkpoter.graduationproject.view.BGQDistributionView;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public class DistributionViewHolderHelper extends BaseAdapterHelper {

    private BGQDistributionView distributionView;
    private MapView arcgisMap;
    private ArcGISMap mMap;

    private FloatingActionButton disclear;
    private FloatingActionButton layerKey;

    private double MAP_SCALE;
    private double AP_SCALE = 50;

    public DistributionViewHolderHelper(ActivityContext context) {
        super(context);
    }

    @Override
    public void initView(View itemView) {
        distributionView = itemView.findViewById(R.id.bgqdisview);
        arcgisMap = itemView.findViewById(R.id.dismapviw);
        disclear = itemView.findViewById(R.id.disclear);
        layerKey = itemView.findViewById(R.id.layerkey);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void doBusiness() {
        initMap();
        disclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (distributionView.getVisibility() != View.GONE) {
                    Toast.makeText(getActivityContext().getContext(), "当前图层不允许刷新加载！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((RecyclerView) getActivityContext().getView()).getAdapter().notifyDataSetChanged();
            }
        });
        distributionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                distributionView.addPoint(new Point(
                                Math.round(motionEvent.getX()), Math.round(motionEvent.getY())),
                        (AP_SCALE / MAP_SCALE) * 100);
                return false;
            }
        });
        layerKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (distributionView.getVisibility() == View.GONE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext().getContext());
                    View view1 = LayoutInflater.from(getActivityContext().getContext()).inflate(R.layout.show_alertdialog, null);
                    builder.setView(view1);
                    EditText sca = view1.findViewById(R.id.show_dialog_ed);
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!sca.getText().toString().equals("")) {
                                MAP_SCALE = Integer.parseInt(sca.getText().toString());
                                layerKey.setImageResource(R.drawable.ic_layers_clear_black_24dp);
                                distributionView.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivityContext().getContext(), "请设置比例尺", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Toast.makeText(getActivityContext().getContext(), "请设置比例尺", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                } else {
                    layerKey.setImageResource(R.drawable.ic_layers_black_24dp);
                    distributionView.setVisibility(View.GONE);
                    distributionView.clear();
                }
            }
        });
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

        symbolizeShapefile(path.replaceAll("file:/", ""));

        arcgisMap.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {

            }
        });
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
}
