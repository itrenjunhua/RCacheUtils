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
 * 描述：缓存管理大小控制线程，用于控制缓存大小，当超过指定大小时，就删除老的文件
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
/*public*/ class RCacheSizeControl extends Thread {
    // 用于临时保存所有的缓存文件对象
    private List<File> cacheFiles = Collections.synchronizedList(new LinkedList<File>());
    // 缓存占用的大小
    private AtomicLong cacheSize = new AtomicLong();

    @Override
    public void run() {
        cacheFiles.clear();
        cacheSize.set(0);
        handlerCacheSize();
    }

    /**
     * 处理缓存大小和删除文件
     */
    private void handlerCacheSize() {
        cacheSize();

        if (cacheSize.get() > RCacheConfig.CACHE_SIZE)
            deleteFileToCacheSize();
    }

    /**
     * 计算缓存大小
     */
    private void cacheSize() {
        if (CacheManageUtils.CACHE_PATH == null || !CacheManageUtils.CACHE_PATH.exists() || !CacheManageUtils.CACHE_PATH.isDirectory()) return;

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
            if (temp <= RCacheConfig.CACHE_SIZE)
                break;
        }

        for (File deleteFile : deleteFiles) {
            RCacheOperatorUtils.deleteFile(deleteFile);
            cacheFiles.remove(deleteFile);
        }
    }
}
