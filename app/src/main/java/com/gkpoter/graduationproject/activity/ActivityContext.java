package com.gkpoter.graduationproject.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public class ActivityContext {

    private Activity activity;
    private Context context;
    private View view;

    public ActivityContext(Activity activity, Context context, View view) {
        this.activity = activity;
        this.context = context;
        this.view = view;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
