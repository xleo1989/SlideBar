package com.x.leo.slidebar;

import android.content.Context;

/**
 * @作者:My
 * @创建日期: 2017/7/12 15:44
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class Utils {
    public static float dp2px(Context ctx,int dp){
        return ctx.getResources().getDisplayMetrics().density * dp;
    }
    public static float sp2px(Context ctx,int sp){
        return ctx.getResources().getDisplayMetrics().scaledDensity * sp;
    }
    public static int px2dp(Context ctx,float px){
        return (int) (px / ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int px2sp(Context ctx,int px){
        return (int) (px/ctx.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }
}
