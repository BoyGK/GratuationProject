package com.gkpoter.graduationproject.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by "nullpointexception0" on 2019/1/22.
 */
public class BGQPositioningView extends BGQDistributionView {

    public BGQPositioningView(Context context) {
        this(context, null);
    }

    public BGQPositioningView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGQPositioningView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
