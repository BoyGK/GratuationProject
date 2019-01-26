package com.gkpoter.graduationproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.gkpoter.graduationproject.activity.MainMenuActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        requestPermission();
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    /**
     * 动态申请。。。。。。。。。。。。。。。。。。。。。。。。。
     */
    /**
     * Request read permission on the device.
     */
    private void requestPermission() {
        // define permission to request
        String[] reqPermission = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this, reqPermission[0])
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    reqPermission,
                    1);
        }
//        if (ContextCompat.checkSelfPermission(MainMenuActivity.this, reqPermission[1])
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainMenuActivity.this,
//                    reqPermission,
//                    MY_PERMISSIONS_REQUEST_WIFI);
//        }
    }

    /**
     * Handle the permissions request response.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            System.exit(0);
        } else {
            startActivity(new Intent(this, MainMenuActivity.class));
        }
    }
}
