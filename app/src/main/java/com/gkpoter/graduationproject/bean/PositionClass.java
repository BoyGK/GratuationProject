package com.gkpoter.graduationproject.bean;

/**
 * Created by "nullpointexception0" on 2019/1/22.
 */
public class PositionClass {

    // TODO: 2019/1/22 定位询问返回信息
    private int state;
    private DataClass.MyPoint point;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public DataClass.MyPoint getPoint() {
        return point;
    }

    public void setPoint(DataClass.MyPoint point) {
        this.point = point;
    }
}
