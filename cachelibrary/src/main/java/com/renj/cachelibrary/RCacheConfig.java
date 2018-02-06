package com.renj.cachelibrary;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-01-21   15:41
 * <p>
 * 描述：缓存管理中的配置接口
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
/*public*/ interface RCacheConfig {
    // 根据CPU确定线程池的线程个数
    int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    int THREAD_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    /**
     * 指定缓存大小 默认大小12M
     */
    long CACHE_SIZE = 1024 * 1024 * 12;
    /**
     * 时间和具体内容之间的分隔符，尽量避免具体内容中可能出现的值
     */
    String SPLIT_CHAR = "&-=SpLiTcHaR=-&";
    /**
     * 文件内容过期标记
     */
    String OUT_TIME_FLAG = "&-=OUT_TIME_FLAG=-&";
    /**
     * 缓存文件扩展名
     */
    String EXTEND_NAME = ".cache";
    /**
     * 当需要在子线程中工作时使用的线程池,默认使用 4 个线程
     */
    ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    /**
     * 主线程的Handler
     */
    Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
}
