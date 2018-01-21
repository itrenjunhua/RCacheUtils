package com.renj.cachelibrary;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-01-21   16:46
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class CacheManageUtils {
    /**
     * 缓存路径
     */
    static File CACHE_PATH;
    /**
     * 缓存大小检查和删除文件线程
     */
    static RCacheSizeControl RCACHE_SIZE_CONTROL;
    /**
     * 每秒的毫秒数
     */
    private static final long SECOND = 1000;
    /**
     * CacheManageUtils 实例对象
     */
    private static CacheManageUtils instance;

    private CacheManageUtils(Context context, String fileName) {
        CACHE_PATH = new File(context.getCacheDir(), fileName);
        if (!CACHE_PATH.exists() || !CACHE_PATH.isDirectory())
            CACHE_PATH.mkdir();

        RCACHE_SIZE_CONTROL = new RCacheSizeControl();
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context 上下文
     */
    public static void initCacheUtil(@NonNull Context context) {
        initCacheUtil(context, "RCache");
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context  上下文
     * @param fileName 缓存目录的名称
     */
    public static void initCacheUtil(@NonNull Context context, @NonNull String fileName) {
        if (instance == null) {
            synchronized (CacheManageUtils.class) {
                if (instance == null) {
                    instance = new CacheManageUtils(context, fileName);
                }
            }
        }
    }

    /**
     * 获取 {@link CacheManageUtils} 实例对象，在调用该方法前，必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @return {@link CacheManageUtils} 实例对象
     * @throws IllegalStateException 在调用该方法前没有调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     *                               或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化时抛出。
     */
    public static CacheManageUtils newInstance() {
        if (instance == null)
            throw new IllegalStateException("没有对 CacheManageUtils 进行初始化，需要先调用 CacheManageUtils.initCacheUtil(Context) " +
                    "或 CacheManageUtils.initCacheUtil(Context，String) 方法。建议在 Application 中调用。");
        return instance;
    }
}
