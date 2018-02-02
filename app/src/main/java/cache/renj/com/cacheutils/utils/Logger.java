package cache.renj.com.cacheutils.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import cache.renj.com.cacheutils.AppConfig;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-02   15:56
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class Logger {
    /**
     * Log日子的 Tag，默认 Logger
     */
    private static String TAG = "Logger";
    /**
     * 是否打印全部类名(类的全路径名)，默认false
     */
    private static boolean IS_FULL_CLASSNAME;

    /**
     * 是否 debug 版本，true 是调试版本；false 是正式版本
     */
    private static boolean isDebug = AppConfig.IS_DEBUG;

    /**
     * 设置是否打印类的全路径名
     *
     * @param isFullClassName true：打印类的全路径名
     */
    public static void isFullClassName(boolean isFullClassName) {
        Logger.IS_FULL_CLASSNAME = isFullClassName;
    }

    /**
     * 设置Log的Tag，默认 "Logger"
     *
     * @param tag
     */
    public static void setAppTAG(@NonNull String tag) {
        Logger.TAG = tag;
    }


    public static void v(@NonNull String msg) {
        if (isDebug) {
            Log.v(TAG, getLogTitle() + msg);
        }
    }


    public static void d(@NonNull String msg) {
        if (isDebug) {
            Log.d(TAG, getLogTitle() + msg);
        }
    }

    public static void i(@NonNull String msg) {
        if (isDebug) {
            Log.i(TAG, getLogTitle() + msg);
        }
    }

    public static void w(@NonNull String msg) {
        if (isDebug) {
            Log.w(TAG, getLogTitle() + msg);
        }
    }

    public static void e(@NonNull String msg) {
        if (isDebug) {
            Log.e(TAG, getLogTitle() + msg);
        }
    }

    /**
     * 返回类名(根据是否设置了打印全类名返回响应的值)，方法名和日子打印所在行数
     *
     * @return (全)类名.方法名(所在行数):
     */
    @NonNull
    private static String getLogTitle() {
        StackTraceElement elm = Thread.currentThread().getStackTrace()[4];
        String className = elm.getClassName();
        if (!IS_FULL_CLASSNAME) {
            int dot = className.lastIndexOf('.');
            if (dot != -1) {
                className = className.substring(dot + 1);
            }
        }
        return className + "." + elm.getMethodName() + "(" + elm.getLineNumber() + ")" + ": ";
    }
}
