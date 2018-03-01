package com.cretin.www.dragdemo;

import android.graphics.Point;

/**
 * Created by cretin on 2018/2/28.
 * 记录recyclerview的位置信息
 */

public class ItemPositionModel {
    private int left;
    private int top;
    private int right;
    private int bottom;
    private Point center;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public ItemPositionModel() {
        center = new Point(0, 0);
    }

    public ItemPositionModel(int left, int top, int right, int bottom,int position) {
        this.position = position;
        center = new Point((left + right) / 2, (top + bottom) / 2);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
}
