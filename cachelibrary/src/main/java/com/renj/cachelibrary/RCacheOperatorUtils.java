package com.renj.cachelibrary;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-01-21   16:52
 * <p>
 * 描述：缓存管理辅助操作的工具类
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class RCacheOperatorUtils {
    /**
     * 计算文件大小，如果是文件夹返回0
     *
     * @param listFile
     * @return 文件大小，如果是文件夹返回 0
     */
    static long calculateFileSize(@NonNull File listFile) {
        if (listFile != null && listFile.exists() && listFile.isFile())
            return listFile.length();
        return 0;
    }

    /**
     * 删除文件
     *
     * @param deleteFile
     * @return 删除的文件长度
     */
    static long deleteFile(@NonNull File deleteFile) {
        long length = deleteFile.length();
        deleteFile.delete();
        return length;
    }
}
