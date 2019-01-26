package com.gkpoter.graduationproject.adapter;

import android.view.View;

import com.gkpoter.graduationproject.activity.ActivityContext;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public abstract class BaseAdapterHelper {

    protected ActivityContext context;

    public BaseAdapterHelper(ActivityContext context) {
        this.context = context;
    }

    public ActivityContext getActivityContext() {
        return context;
    }

    abstract void initView(View itemView);

    abstract void doBusiness();
}
