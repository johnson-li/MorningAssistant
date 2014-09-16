package com.johnson;

/**
 * Created by johnson on 9/12/14.
 * This class is used to replace android.util.Log.d with one feature from log4j
 */
public class Log {

    static String getLogTag() {
        return getSimpleClassName(3);
    }

    static String getClassName(int level) {
        return new Throwable().getStackTrace()[level].getClassName();
    }

    static String getSimpleClassName(int level) {
        String className = new Throwable().getStackTrace()[level].getClassName();
        return className.substring(className.lastIndexOf(".") + 1);
    }

    static String getMethodName(int level) {
        return new Throwable().getStackTrace()[level].getMethodName();
    }

    public static void d(String str) {
        android.util.Log.d(getLogTag(), str);
    }

    public static void i(String str) {
        android.util.Log.i(getLogTag(), str);
    }

    public static void e(String str) {
        android.util.Log.e(getLogTag(), str);
    }

    public static void v(String str) {
        android.util.Log.v(getLogTag(), str);
    }

    public static void w(String str) {
        android.util.Log.w(getLogTag(), str);
    }

    public static void d(Object object) {
        android.util.Log.d(getLogTag(), object.toString());
    }

    public static void i(Object object) {
        android.util.Log.i(getLogTag(), object.toString());
    }

    public static void e(Object object) {
        android.util.Log.e(getLogTag(), object.toString());
    }

    public static void v(Object object) {
        android.util.Log.v(getLogTag(), object.toString());
    }

    public static void w(Object object) {
        android.util.Log.w(getLogTag(), object.toString());
    }
}
