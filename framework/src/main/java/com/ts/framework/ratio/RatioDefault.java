package com.ts.framework.ratio;

/**
 * 存储份额随机的数据
 * @author wl
 */
public class RatioDefault implements Ratio {
    private int id;
    private int ratio;

    public RatioDefault() {
    }

    public RatioDefault(int id, int ratio) {
        this.id = id;
        this.ratio = ratio;
    }

    @Override
    public int getRatio() {
        return ratio;
    }

    @Override
    public String toString() {
        return id + ":" + ratio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

}