package com.example.lyx.lweather.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/6/13.
 */

public class LogUtil {
    public final static int VERBOSE=1;
    public final static int DEBUG=2;
    public final static int INFO=3;
    public final static int WARN=4;
    public final static int ERROR=5;
    public final static int NOTHING=6;

    public static int LEVEL=VERBOSE;

    public static void v(String tag,String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag,msg);
        }
    }
    public static void d(String tag,String msg) {
        if (LEVEL <= DEBUG) {
            Log.v(tag,msg);
        }
    }
    public static void i(String tag,String msg) {
        if (LEVEL <= INFO) {
            Log.v(tag,msg);
        }
    }
    public static void w(String tag,String msg) {
        if (LEVEL <= WARN) {
            Log.v(tag,msg);
        }
    }
    public static void e(String tag,String msg) {
        if (LEVEL <= ERROR) {
            Log.v(tag,msg);
        }
    }
}
