package com.x.leo.slidebar;

import android.view.View;

/**
 * @作者:My
 * @创建日期: 2017/6/1 11:39
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface OnSlideDrag {
    void onDragStart(View v);

    void onDraging(View v, int percent);

    void onDragEnd(View v,boolean isComplete);
}
