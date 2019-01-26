package com.gkpoter.graduationproject.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.adapter.MainMenuAdapter;
import com.gkpoter.graduationproject.utils.FileHelper;
import com.gkpoter.graduationproject.utils.FileUtils;
import com.gkpoter.graduationproject.utils.HttpRequest;
import com.gkpoter.graduationproject.utils.SharedPreferencesHelper;
import com.gkpoter.graduationproject.view.BGQLayoutManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TYPE_MENU = 0;
    private MainMenuAdapter adapter;
    private RecyclerView recyclerView;
    private BGQLayoutManager layoutManager;
    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(TYPE_MENU);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.main_recyclerview);
        adapter = new MainMenuAdapter(new ActivityContext(this, this, recyclerView));
        recyclerView.setAdapter(adapter);
        layoutManager = new BGQLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        sharedPreferencesHelper
                = new SharedPreferencesHelper("settings", getApplicationContext());
        if ("1".equals(sharedPreferencesHelper.getData("lock", ""))) {
            menu.getItem(0).setTitle(R.string.action_unlocking);
            layoutManager.setScrollKey(false);
        } else if ("0".equals(sharedPreferencesHelper.getData("lock", ""))) {
            menu.getItem(0).setTitle(R.string.action_locking);
            layoutManager.setScrollKey(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_locking) {
            if (!layoutManager.canScrollVertically()) {
                item.setTitle(R.string.action_locking);
                layoutManager.setScrollKey(true);
                sharedPreferencesHelper.saveData("lock", "0");
            } else {
                item.setTitle(R.string.action_unlocking);
                layoutManager.setScrollKey(false);
                sharedPreferencesHelper.saveData("lock", "1");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_folder) {
            getFilePath();
        } else if (id == R.id.nav_clear) {
            new FileHelper().clear("wifi.txt");
            Toast.makeText(this, "采集信息清空成功", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_wifi) {
            startActivity(new Intent(getApplicationContext(), WifiManagerActivity.class));
        } else if (id == R.id.nav_upload) {
            new FileHelper().save("wifi.txt", "]");
            RequestParams params = new RequestParams();
            File file = new File(Environment.getExternalStorageDirectory() + "/wifi.txt");
            try {
                params.put("file", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            HttpRequest.post(getApplicationContext(), "/upload", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(MainMenuActivity.this, new String(responseBody), Toast.LENGTH_SHORT).show();
                    new FileHelper().clear("wifi.txt");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(MainMenuActivity.this, "上传失败，网络问题", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_location) {
            //startActivity(new Intent(getApplicationContext(), PositioningActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 选择文件路径
     */
    private static final int FILE_SELECT_CODE = 0;

    //选择文件路径并且存储
    private void getFilePath() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null) {
                        String path = FileUtils.getFilePathByUri(getApplicationContext(), data.getData());
                        System.out.println(path);
                        File file = new File(path);
                        new SharedPreferencesHelper("shpPath", getApplicationContext())
                                .saveData("shp", path);
                        new SharedPreferencesHelper("shpInfo", getApplicationContext())
                                .saveData("name", file.getName());
                        Toast.makeText(this, "获取文件成功" + path, Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(1);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
