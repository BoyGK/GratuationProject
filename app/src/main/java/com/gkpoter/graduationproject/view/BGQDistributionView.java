package com.gkpoter.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by "nullpointexception0" on 2019/1/17.
 */
public class BGQDistributionView extends View {

    private int ap_point = Color.RED;
    private int point_to_line = 0x600064FF;
    private Point[] points = null;
    private double R;

    public BGQDistributionView(Context context) {
        this(context, null);
    }

    public BGQDistributionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGQDistributionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (points != null && points.length != 0) {
            float r = (float) R;
            Paint paint = new Paint();

            paint.setColor(ap_point);
            paint.setStrokeWidth(10);
            canvas.drawPoints(point2float(points), paint);


            paint.setColor(point_to_line);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
            for (int i = 0; i < points.length; i++) {
                canvas.drawCircle(points[i].x, points[i].y, r, paint);
            }
        }
    }

    private float[] point2float(Point[] points) {
        float[] floats = new float[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            floats[2 * i] = points[i].x;
            floats[2 * i + 1] = points[i].y;
        }
        return floats;
    }

    public void setApPointColor(int i) {
        this.ap_point = i;
    }

    public void setFillColor(int i) {
        this.point_to_line = i;
    }

    public void drawDistributionLayer(Point[] points, double r) {
        this.points = points;
        this.R = r;
        invalidate();
    }

    public void addPoint(Point p, double r) {
        if (points != null) {
            Point[] ps = new Point[points.length + 1];
            System.arraycopy(points, 0, ps, 0, points.length);
            ps[points.length] = p;
            points = ps;
        } else {
            points = new Point[1];
            points[0] = p;
        }
        this.R = r;
        invalidate();
    }

    public void clear() {
        this.points = null;
        this.R = 0;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
