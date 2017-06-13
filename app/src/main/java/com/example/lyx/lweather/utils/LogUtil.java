package com.example.lyx.lweather.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/6/13. Log处理
 */

public class LogUtil {
    public final static int VERBOSE=1;
    public final static int DEBUG=2;
    public final static int INFO=3;
    public final static int WARN=4;
    public final static int ERROR=5;
    public final static int NOTHING=6;

    public static int LEVEL=DEBUG;

    public static void v(String tag,String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag,msg);
        }
    }
    public static void d(String tag,String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag,msg);
        }
    }
    public static void i(String tag,String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag,msg);
        }
    }
    public static void w(String tag,String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag,msg);
        }
    }
    public static void e(String tag,String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag,msg);
        }
    }
}
