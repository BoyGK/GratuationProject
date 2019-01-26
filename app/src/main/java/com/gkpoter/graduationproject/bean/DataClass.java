package com.gkpoter.graduationproject.bean;

import java.util.List;

/**
 * Created by nullpointexception0 on 2019/1/16.
 */

public class DataClass {

    /**
     * mapId:"asd"
     * point:{x:1,y:2}
     * MAC : sds
     * APs : [{"name":"sdsd","MAC":"sd:ssd:SdSS:sd","RSSI":1212,"frequency":121}]
     */

    private String mapId;
    private MyPoint point;
    private String MAC;
    private List<APsBean> APs;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public MyPoint getPoint() {
        return point;
    }

    public void setPoint(MyPoint point) {
        this.point = point;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public List<APsBean> getAPs() {
        return APs;
    }

    public void setAPs(List<APsBean> APs) {
        this.APs = APs;
    }

    public static class MyPoint {

        //百分比坐标范围0--1
        private double x;
        private double y;

        public MyPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static class APsBean {
        /**
         * name : sdsd
         * MAC : sd:ssd:SdSS:sd
         * RSSI : 1212
         * frequency : 121
         */

        private String name;
        private String MAC;
        private int RSSI;
        private int frequency;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMAC() {
            return MAC;
        }

        public void setMAC(String MAC) {
            this.MAC = MAC;
        }

        public int getRSSI() {
            return RSSI;
        }

        public void setRSSI(int RSSI) {
            this.RSSI = RSSI;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }
}
