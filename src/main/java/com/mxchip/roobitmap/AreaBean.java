package com.mxchip.roobitmap;

/**
 * 绘图Bean
 * Created by StramChen on 2018/3/7.
 */

public class AreaBean {
    private int id;
    private float x;
    private float y;
    private int t;

    public AreaBean(){

    }
    public AreaBean(int id, float x, float y, int t) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.t = t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
