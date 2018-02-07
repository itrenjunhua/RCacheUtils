package com.renj.cachelibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
/*public*/ class RCacheOperatorUtils {

    /**
     * 基于缓存路径 {@link CacheManageUtils#CACHE_PATH} 统一拼接文件扩展名
     *
     * @param fileName 文件名
     * @return 带扩展名的 File 对象
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static File spliceFile(@NonNull String fileName) {
        File file = new File(CacheManageUtils.CACHE_PATH, fileName.hashCode() + RCacheConfig.EXTEND_NAME);
        return file;
    }

    /**
     * 给字符串内容增加有效期
     *
     * @param value   保存内容
     * @param outtime 有效时间
     * @return 按照特殊格式增加了有效时间的内容(增加的时间表示最终有效期)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static String addDateInfo(@NonNull String value, long outtime) {
        return createDateInfo(outtime) + value;
    }

    /**
     * 清除内容为字符串的时间信息
     *
     * @param value
     * @return 返回清除过期时间之后的内容
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static String clearDateInfo(@NonNull String value) {
        if (value != null) {
            String[] strings = value.split(RCacheConfig.SPLIT_CHAR);
            if (strings.length == 2) {
                if (currentTimeMillis() <= Long.parseLong(strings[0]))
                    return strings[1];
                else return RCacheConfig.OUT_TIME_FLAG;
            }
            return value;
        }
        return "";
    }

    /**
     * 给字节数组内容增加有效期
     *
     * @param value   保存内容
     * @param outtime 有效时间
     * @return 按照特殊格式增加了有效时间的内容(增加的时间表示最终有效期)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    static byte[] addDateInfo(@NonNull byte[] value, long outtime) {
        if (value == null) return null;

        byte[] dateInfoBytes = toBytes(createDateInfo(outtime));
        byte[] newBytes = new byte[dateInfoBytes.length + value.length];
        System.arraycopy(dateInfoBytes, 0, newBytes, 0, dateInfoBytes.length);
        System.arraycopy(value, 0, newBytes, dateInfoBytes.length, value.length);
        return newBytes;
    }

    /**
     * 清除内容为字符串的时间信息
     *
     * @param value
     * @return 返回清除过期时间之后的内容
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    static byte[] clearDateInfo(@NonNull byte[] value) {
        if (value != null) {
            byte[] splitBytes = toBytes(RCacheConfig.SPLIT_CHAR);
            int startIndex = 13 + splitBytes.length;
            // 判断长度是否大于时间和分割符的字节数组和
            if (value.length <= startIndex) return value;
            // 取出过期时间值和当前系统时间作比较
            if (currentTimeMillis() <= Long.parseLong(new String(value, 0, 13))) {
                byte[] resultValue = new byte[value.length - startIndex];
                System.arraycopy(value, startIndex, resultValue, 0, value.length - startIndex);
                return resultValue;
            } else {
                return toBytes(RCacheConfig.OUT_TIME_FLAG);
            }
        }
        return null;
    }

    /**
     * 判断内容为字符串的缓存是否有有效期限制
     *
     * @param value
     * @return
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static boolean isTimeLimit(@NonNull String value) {
        return value.contains(RCacheConfig.SPLIT_CHAR);
    }

    /**
     * 判断内容为字节数组的缓存是否有有效期限制
     *
     * @param value
     * @return
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    @org.jetbrains.annotations.Contract(value = "null -> false")
    static boolean isTimeLimit(@NonNull byte[] value) {
        // 判断长度小于14的原因是使用自定义的currentTimeMillis()方法获取到的时间值为13位
        if (value == null || value.length < 14) {
            return false;
        }
        byte[] splitBytes = toBytes(RCacheConfig.SPLIT_CHAR);
        int length = splitBytes.length;
        // 重第13的角标开始是去掉前面表示时间值得13位，重第14位开始表示分隔符
        int index = 13;
        for (int i = 0; i < length; i++) {
            if (value[index + i] != splitBytes[i])
                return false;
        }
        return true;
    }

    /**
     * 判断2个字节数组是否一样
     *
     * @param bytes1
     * @param bytes2
     * @return
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    @org.jetbrains.annotations.Contract(value = "null,null -> false")
    static boolean equalsBytes(@NonNull byte[] bytes1, @NonNull byte[] bytes2) {
        if (bytes1 == bytes2) return true;
        if (bytes1 == null || bytes2 == null) return false;
        if (bytes1.length != bytes2.length) return false;

        int length = bytes1.length;
        for (int i = 0; i < length; i++) {
            if (bytes1[i] != bytes2[i])
                return false;
        }
        return true;
    }

    /**
     * 创建时间信息字符串，过期时间+分割内容 {@link RCacheConfig#SPLIT_CHAR}
     *
     * @param outtime
     * @return
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static String createDateInfo(@NonNull long outtime) {
        return (currentTimeMillis() + outtime) + RCacheConfig.SPLIT_CHAR;
    }

    /**
     * 获取13位的时间
     *
     * @return
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static long currentTimeMillis() {
        String currentTimeMillis = System.currentTimeMillis() + "";
        while (currentTimeMillis.length() < 13) {
            currentTimeMillis = currentTimeMillis + "0";
        }
        return Long.parseLong(currentTimeMillis);
    }

    /**
     * 将指定字符串按 utf-8 编码为字节数组
     *
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static byte[] toBytes(@NonNull String value) {
        if (value == null) value = "";
        try {
            return value.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value.getBytes();
        }
    }

    /**
     * 计算文件大小，如果是文件夹返回0
     *
     * @param listFile
     * @return 文件大小，如果是文件夹返回 0
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
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
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    static long deleteFile(@NonNull File deleteFile) {
        if (deleteFile == null || !deleteFile.exists()) return 0;
        long length = deleteFile.length();
        deleteFile.delete();
        return length;
    }

    /**
     * 清除文件中的内容
     *
     * @param file
     */
    static void clearContent(@NonNull File file) {
        FileWriter fileWriter = null;
        try {
            if (file.length() > 0) {
                fileWriter = new FileWriter(file);
                fileWriter.write("");
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查缓存文件大小和删除文件线程
     */
    static synchronized void checkCacheSize() {
        if (CacheManageUtils.RCACHE_SIZE_CONTROL != null && !CacheManageUtils.RCACHE_SIZE_CONTROL.isExecuteing) {
            RCacheConfig.EXECUTORSERVICE.execute(CacheManageUtils.RCACHE_SIZE_CONTROL);
        }
    }

    /**
     * 将 Drawable 转换为 Bitmap
     *
     * @param drawable
     * @return
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        if (drawable == null) return null;

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable.getOpacity() 获取不透明度
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将 Bitmap 转换为 Drawable
     *
     * @param bitmap
     * @return
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    static Drawable bitmapToDrawable(@NonNull Bitmap bitmap) {
        if (bitmap == null) return null;

        return new BitmapDrawable(bitmap);
    }

    /**
     * 将 Bitmap 转换为 字节数组 byte[]
     *
     * @param bitmap
     * @return
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    static byte[] bitmapToBytes(@NonNull Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 将 字节数组byte[] 变换为 Bitmap
     *
     * @param bytes
     * @return
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    static Bitmap bytesToBitmap(@NonNull byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
