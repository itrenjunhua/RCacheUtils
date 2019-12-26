package com.renj.test.app;

import android.app.Application;
import android.os.Handler;

import com.renj.cache.RCacheManageUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-02   15:04
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class MyApplication extends Application {
    public static MyApplication application;
    private Handler handler = new Handler();
    private Thread thread = Thread.currentThread();

    @Override
    public void onCreate() {
        super.onCreate();

        this.application = this;

        // 初始化缓存库
        RCacheManageUtils.initCacheUtil(this, "CacheTest");
    }

    public Handler getMainHandler(){
        return handler;
    }

    public Thread getMainThread(){
        return thread;
    }
}
