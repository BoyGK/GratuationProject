package com.gkpoter.graduationproject.adapter;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.activity.ActivityContext;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public class StepViewHolderHelper extends BaseAdapterHelper
        implements View.OnTouchListener, SensorEventListener {

    private CardView stepStart;
    private TextView stepCount;
    private FloatingActionButton fresh;

    private SensorManager mSensorManager;
    private Sensor mStepDetector;
    private int mDetector = 0;

    private int CR = 115, CG = 115, CB = 115;

    public StepViewHolderHelper(ActivityContext context) {
        super(context);
    }

    @Override
    public void initView(View itemView) {
        stepStart = itemView.findViewById(R.id.stepstart);
        stepCount = itemView.findViewById(R.id.stepCount);
        fresh = itemView.findViewById(R.id.stepfresh);
    }

    @Override
    public void doBusiness() {
        stepStart.setOnTouchListener(this);
        mSensorManager = (SensorManager) getActivityContext().getActivity().getSystemService(SENSOR_SERVICE);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //mSensorManager.registerListener(this, mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
        //mSensorManager.unregisterListener(this, mStepCount);

        fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetector = 0;
                stepCount.setText(mDetector + "");
                stepCount.setTextColor(Color.rgb(CR, CG, CB));
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.stepstart) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stepStart.setCardElevation(8);
                    mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
                    break;
                case MotionEvent.ACTION_UP:
                    stepStart.setCardElevation(18);
                    mSensorManager.unregisterListener(this, mStepDetector);
                    break;
            }
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (sensorEvent.values[0] == 1.0) {
                mDetector++;
                if (mDetector > 99) {
                    stepCount.setText("无效");
                    return;
                }
                stepCount.setText(mDetector + "");
                stepCount.setTextColor(Color.rgb(
                        CR + mDetector * 2 > 255 ? 255 : CR + mDetector * 2,
                        CG - mDetector * 2 < 0 ? 0 : CG - mDetector * 2,
                        CB - mDetector * 2 < 0 ? 0 : CB - mDetector * 2));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
