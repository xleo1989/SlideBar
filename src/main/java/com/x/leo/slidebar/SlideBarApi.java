package com.x.leo.slidebar;

/**
 * @作者:My
 * @创建日期: 2017/7/12 15:12
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface SlideBarApi {
    void setMinimum(double minimum);
    void setMaximum(double maxmun);
    double getMaximum();
    double getMinimum();
    float getCurrentPerception();
    double getCurrentValue();
    void setOnDragCallBack(OnSlideDrag l);
}
