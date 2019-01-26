package com.gkpoter.graduationproject.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gkpoter.graduationproject.R;

import java.util.List;

/**
 * Created by "GKpoter" on 2017/8/3.
 */

public class WifiManagerAdapter extends BaseAdapter {

    private List<ScanResult> scanResults;
    private Context context;

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    public WifiManagerAdapter(List<ScanResult> scanResults, Context context) {
        this.scanResults = scanResults;
        this.context = context;
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.listview_wifi_content, null, false);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView mac_name = (TextView) convertView.findViewById(R.id.mac_name);
        TextView dbm = (TextView) convertView.findViewById(R.id.dbm);
        name.setText("name:" + scanResults.get(position).SSID);
        mac_name.setText("mac:" + scanResults.get(position).BSSID);
        dbm.setText("level:" + scanResults.get(position).level + "dbm");
        return convertView;
    }
}
