package com.gkpoter.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by "nullpointexception0" on 2019/1/13.
 */
public class BGQMapView extends View {

    public static int COLLECTION_WALK = 2;
    public static int COLLECTION_STOP = 1;
    private int WALK_OR_STOP = COLLECTION_STOP;
    private boolean isTouch = false;

    private float x1, x2, y1, y2;
    private Paint paint = new Paint();

    public BGQMapView(Context context) {
        this(context, null);
    }

    public BGQMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
    }

    public void changeCollectionWay(int i) {
        WALK_OR_STOP = i;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (WALK_OR_STOP == 1) {
            if (x1 != 0 && y1 != 0) {
                canvas.drawCircle(x1, y1, 10, paint);
            }
        } else if (WALK_OR_STOP == 2) {
            if (x1 != 0 && y1 != 0) {
                //canvas.drawCircle(x1, y1, 10, paint);
                canvas.drawRect(x1 - 10, y1 - 10, x1 + 10, y1 + 10, paint);
            }
            if (x2 != 0 && y2 != 0) {
                canvas.drawCircle(x2, y2, 10, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (WALK_OR_STOP == 1) {
            x1 = event.getX();
            y1 = event.getY();
        } else if (WALK_OR_STOP == 2) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isTouch) {
                        x1 = event.getX();
                        y1 = event.getY();
                        isTouch = true;
                    } else {
                        x2 = event.getX();
                        y2 = event.getY();
                        isTouch = false;
                    }
                    break;
            }
        }
        invalidate();
        return true;
    }
}
