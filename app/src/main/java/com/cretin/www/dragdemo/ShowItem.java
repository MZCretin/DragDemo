package com.cretin.www.dragdemo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.util.Random;

/**
 * Created by xiangcheng on 17/11/30.
 */
public class ShowItem {
    public Drawable color;
    public String des;
    //0 正常 1 间隔
    private int type;
    // 0 不管 1 正确 2 错误
    private int isRight;

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ShowItem(String des, int type) {
        this.type = type;
        this.des = des;
        color = getBack();
    }

    private Drawable getBack() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(8);
        drawable.setColor(Color.rgb(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
        return drawable;
    }
}