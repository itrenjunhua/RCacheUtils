package com.renj.cachelibrary;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-01-21   16:48
 * <p>
 * 描述：缓存管理大小控制线程，用于控制缓存大小，当超过指定大小时，就删除老的文件。<br/>
 * <b>注意：</b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <b>因为没次做保存数据操作时都会调用检查和删除缓存的线程，所以可能导致多个线程同时执行从而报错；
 * 因此在 {@link RCacheSizeControl} 类中增加 {@link #isExecuteing} 字段作标记</b>(因为使用的线程池，
 * 且每次开启时都是将 {@link CacheManageUtils#RCACHE_SIZE_CONTROL} 这个对象传递到线程池中执行，
 * 所以直接将{@link #isExecuteing} 字段放在 {@link RCacheSizeControl} 类中，而不是作为全局变量操作)，
 * <b>保证检查和删除缓存的线程同一时间只有一个执行。</b>
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
/*public*/ class RCacheSizeControl implements Runnable {
    // 用于临时保存所有的缓存文件对象
    private List<File> cacheFiles = Collections.synchronizedList(new LinkedList<File>());
    // 缓存占用的大小
    private AtomicLong cacheSize = new AtomicLong();
    // 标记当前对象是否正在执行(保证检查删除缓存的线程只有一个执行)
    static volatile boolean isExecuteing = false;

    @Override
    public void run() {
        isExecuteing = true;

        cacheFiles.clear();
        cacheSize.set(0);
        handlerCacheSize();

        isExecuteing = false;
    }

    /**
     * 处理缓存大小和删除文件
     */
    private void handlerCacheSize() {
        cacheSize();

        if (cacheSize.get() > CacheManageUtils.caheSize)
            deleteFileToCacheSize();
    }

    /**
     * 计算缓存大小
     */
    private void cacheSize() {
        if (CacheManageUtils.CACHE_PATH == null || !CacheManageUtils.CACHE_PATH.exists() || !CacheManageUtils.CACHE_PATH.isDirectory())
            return;

        File[] listFiles = CacheManageUtils.CACHE_PATH.listFiles();
        for (File listFile : listFiles) {
            long fileSize = RCacheOperatorUtils.calculateFileSize(listFile);
            cacheFiles.add(listFile);
            cacheSize.addAndGet(fileSize);
        }
    }

    /**
     * 删除老文件，直到缓存文件总大小小于指定的缓存大小
     */
    private void deleteFileToCacheSize() {
        // 按修改时间进行排序
        Collections.sort(cacheFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long l = o1.lastModified() - o2.lastModified();
                return l > 0 ? 1 : -1;
            }
        });

        // 循环取出需要删除的文件
        List<File> deleteFiles = new LinkedList<>();
        for (File cacheFile : cacheFiles) {
            long temp = cacheSize.addAndGet(-RCacheOperatorUtils.calculateFileSize(cacheFile));
            deleteFiles.add(cacheFile);
            if (temp <= CacheManageUtils.caheSize)
                break;
        }

        for (File deleteFile : deleteFiles) {
            RCacheOperatorUtils.deleteFile(deleteFile);
            cacheFiles.remove(deleteFile);
        }
    }
}
