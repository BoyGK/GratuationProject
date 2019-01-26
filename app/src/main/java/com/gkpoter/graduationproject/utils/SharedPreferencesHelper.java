package com.gkpoter.graduationproject.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by "nullpointexception0" on 2019/1/14.
 */
public class SharedPreferencesHelper {

    private String documentation;
    private Context context;

    public SharedPreferencesHelper(String documentation, Context context) {
        this.documentation = documentation;
        this.context = context;
    }

    public void saveData(String name, String object) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(documentation, Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString(name, object);
        //提交当前数据
        editor.commit();
    }

    public String getData(String name, String Ex) {
        //同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences(documentation, Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        return sharedPreferences.getString(name, Ex);
    }

    public void clearData() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(documentation, Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

}
