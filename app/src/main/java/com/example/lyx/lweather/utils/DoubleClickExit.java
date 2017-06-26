package com.example.lyx.lweather.utils;

/**
 * Created by Administrator
 * on 2017/6/26.
 * 修改备注：
 */

public class DoubleClickExit {
    public static long mLastClick = 0L;
    private static final int THRESHOLD = 2000;// 1000ms

    public static boolean check() {
        long now = System.currentTimeMillis();
        boolean b = now - mLastClick < THRESHOLD;
        mLastClick = now;
        return b;
    }
}
