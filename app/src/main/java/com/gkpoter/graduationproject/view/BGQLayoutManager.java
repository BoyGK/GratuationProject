package com.gkpoter.graduationproject.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by "nullpointexception0" on 2019/1/14.
 */
public class BGQLayoutManager extends LinearLayoutManager {

    private boolean scrollKey = false;

    public BGQLayoutManager(Context context) {
        super(context);
    }

    public BGQLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public BGQLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollKey(boolean scrollKey) {
        this.scrollKey = scrollKey;
    }

    @Override
    public boolean canScrollVertically() {
        return scrollKey;
    }
}
