package com.renj.cache;

import java.io.File;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * 邮箱：renjunhua@anlovek.com
 * <p>
 * 创建时间：2018-06-01   15:40
 * <p>
 * 描述：清除所有缓存线程
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
/*public*/ class RClearCacheThread implements Runnable {
    // 标记当前对象是否正在执行(保证删除缓存的线程只有一个执行)
    static volatile boolean isExecutingClear = false;

    @Override
    public void run() {
        isExecutingClear = true;

        if (RCacheManageUtils.CACHE_PATH == null || !RCacheManageUtils.CACHE_PATH.exists() || !RCacheManageUtils.CACHE_PATH.isDirectory())
            return;

        File[] listFiles = RCacheManageUtils.CACHE_PATH.listFiles();
        for (File listFile : listFiles) {
            listFile.delete();
        }

        isExecutingClear = false;
    }
}
