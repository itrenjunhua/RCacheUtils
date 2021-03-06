package com.renj.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-01-21   16:46
 * <p>
 * 描述：缓存管理工具类<br/><br/>
 * 主要功能：<br/>&nbsp;&nbsp;&nbsp;&nbsp;
 * 1.提供同步、异步 缓存或者获取 字符串、字节数组、Drawable 和 Bitmap JSONObject、JSONArray、Serializable 内容的方法
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;
 * 2.支持指定缓存时间，缓存时间单位为 秒(s)<br/><br/>
 * <b>使用注意：<br/>&nbsp;&nbsp;&nbsp;&nbsp;
 * 在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
 * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。</b>
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public final class RCacheManageUtils {
    /**
     * 缓存路径
     */
    static File CACHE_PATH;
    /**
     * 缓存大小，默认 {@link RCacheConfig#CACHE_SIZE}
     */
    static long cacheSize = RCacheConfig.CACHE_SIZE;
    /**
     * 缓存大小检查和删除文件线程
     */
    static volatile RCacheSizeControl R_CACHE_SIZE_CONTROL;
    /**
     * 每秒的毫秒数
     */
    private static final long SECOND = 1000;
    /**
     * CacheManageUtils 实例对象
     */
    private static RCacheManageUtils instance;

    private RCacheManageUtils(Context context, String fileName, long cacheSize) {
        CACHE_PATH = new File(context.getCacheDir(), fileName);
        if (!CACHE_PATH.exists() || !CACHE_PATH.isDirectory())
            CACHE_PATH.mkdirs();

        R_CACHE_SIZE_CONTROL = new RCacheSizeControl();
        RCacheManageUtils.cacheSize = cacheSize;
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context 上下文
     */
    @SuppressWarnings("unused")
    public static void initCacheUtil(@NonNull Context context) {
        initCacheUtil(context, RCacheConfig.CACHE_FILE_NAME);
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context  上下文
     * @param fileName 缓存目录的名称
     */
    public static void initCacheUtil(@NonNull Context context, @NonNull String fileName) {
        initCacheUtil(context, fileName, RCacheConfig.CACHE_SIZE);
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context   上下文
     * @param cacheSize 缓存总大小
     */
    public static void initCacheUtil(@NonNull Context context, long cacheSize) {
        initCacheUtil(context, RCacheConfig.CACHE_FILE_NAME, cacheSize);
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context   上下文
     * @param fileName  缓存目录的名称
     * @param cacheSize 缓存总大小
     */
    public static void initCacheUtil(@NonNull Context context, @NonNull String fileName, long cacheSize) {
        if (context == null)
            throw new IllegalArgumentException("参数 context 不能为 null.");

        if (cacheSize < 0)
            throw new IllegalArgumentException("参数 cacheSize 的值必须大于0.");

        if (instance == null) {
            synchronized (RCacheManageUtils.class) {
                if (instance == null) {
                    instance = new RCacheManageUtils(context, fileName, cacheSize);
                }
            }
        }
    }

    /**
     * 获取 {@link RCacheManageUtils} 实例对象，在调用该方法前，必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @return {@link RCacheManageUtils} 实例对象
     * @throws IllegalStateException 在调用该方法前没有调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     *                               或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化时抛出。
     */
    @CheckResult(suggest = "返回值没有使用")
    public static RCacheManageUtils getInstance() {
        if (instance == null)
            throw new IllegalStateException("没有对 CacheManageUtils 进行初始化，需要先调用 CacheManageUtils.initCacheUtil(Context) " +
                    "或 CacheManageUtils.initCacheUtil(Context，String) 方法。建议在 Application 中调用。");
        return instance;
    }

    /**
     * 缓存字符串({@link String})内容，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param value   需要缓存的字符串({@link String})内容
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, String)
     * @see #putOnNewThread(String, String)
     * @see #putOnNewThread(String, String, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull String value, long outTime) {
        return put(key, RCacheOperatorUtils.addDateInfo(value, outTime * SECOND));
    }

    /**
     * 缓存 {@link JSONObject} 对象
     *
     * @param key        缓存键名称，同时用于获取缓存
     * @param jsonObject 需要缓存的 {@link JSONObject} 对象
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b> <b>注意：可能为 {@code null}</b>
     * @see #put(String, JSONObject, long)
     * @see #putOnNewThread(String, JSONObject)
     * @see #putOnNewThread(String, JSONObject, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull JSONObject jsonObject) {
        return put(key, jsonObject.toString());
    }

    /**
     * 缓存 {@link JSONObject} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key        缓存键名称，同时用于获取缓存
     * @param jsonObject 需要缓存的  {@link JSONObject} 对象
     * @param outTime    有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, JSONObject)
     * @see #putOnNewThread(String, JSONObject)
     * @see #putOnNewThread(String, JSONObject, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull JSONObject jsonObject, long outTime) {
        return put(key, RCacheOperatorUtils.addDateInfo(jsonObject.toString(), outTime * SECOND));
    }

    /**
     * 缓存 {@link JSONArray} 对象
     *
     * @param key       缓存键名称，同时用于获取缓存
     * @param jsonArray 需要缓存的  {@link JSONArray} 对象
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, JSONArray, long)
     * @see #putOnNewThread(String, JSONArray)
     * @see #putOnNewThread(String, JSONArray, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull JSONArray jsonArray) {
        return put(key, jsonArray.toString());
    }

    /**
     * 缓存 {@link JSONArray} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key       缓存键名称，同时用于获取缓存
     * @param jsonArray 需要缓存的  {@link JSONArray} 对象
     * @param outTime   有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, JSONArray)
     * @see #putOnNewThread(String, JSONArray)
     * @see #putOnNewThread(String, JSONArray, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull JSONArray jsonArray, long outTime) {
        return put(key, RCacheOperatorUtils.addDateInfo(jsonArray.toString(), outTime * SECOND));
    }

    /**
     * 缓存字节数组(byte[])，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bytes   需要缓存的字节数组(byte[])
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, byte[])
     * @see #putOnNewThread(String, byte[])
     * @see #putOnNewThread(String, byte[], long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull byte[] bytes, long outTime) {
        return put(key, RCacheOperatorUtils.addDateInfo(bytes, outTime * SECOND));
    }

    /**
     * 缓存 {@link Bitmap} 对象
     *
     * @param key    缓存键名称，同时用于获取缓存
     * @param bitmap 需要缓存的  {@link Bitmap} 对象
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Bitmap, long)
     * @see #putOnNewThread(String, Bitmap)
     * @see #putOnNewThread(String, Bitmap, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Bitmap bitmap) {
        return put(key, RCacheOperatorUtils.bitmapToBytes(bitmap));
    }

    /**
     * 缓存 {@link Bitmap} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bitmap  需要缓存的 {@link Bitmap} 对象
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Bitmap)
     * @see #putOnNewThread(String, Bitmap)
     * @see #putOnNewThread(String, Bitmap, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Bitmap bitmap, long outTime) {
        return put(key, RCacheOperatorUtils.addDateInfo(RCacheOperatorUtils.bitmapToBytes(bitmap), outTime * SECOND));
    }

    /**
     * 缓存 {@link Drawable} 对象
     *
     * @param key      缓存键名称，同时用于获取缓存
     * @param drawable 需要缓存的  {@link Drawable} 对象
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Drawable, long)
     * @see #putOnNewThread(String, Drawable)
     * @see #putOnNewThread(String, Drawable, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Drawable drawable) {
        return put(key, RCacheOperatorUtils.drawableToBitmap(drawable));
    }

    /**
     * 缓存 {@link Drawable} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key      缓存键名称，同时用于获取缓存
     * @param drawable 需要缓存的 {@link Drawable} 对象
     * @param outTime  有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Drawable)
     * @see #putOnNewThread(String, Drawable)
     * @see #putOnNewThread(String, Drawable, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Drawable drawable, long outTime) {
        return put(key, RCacheOperatorUtils.drawableToBitmap(drawable), outTime);
    }

    /**
     * 缓存 {@link Serializable} 对象
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param value 需要缓存的  {@link Serializable} 对象
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Serializable, long)
     * @see #putOnNewThread(String, Serializable)
     * @see #putOnNewThread(String, Serializable, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Serializable value) {
        return put(key, value, -1);
    }

    /**
     * 缓存 {@link Serializable} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param value   需要缓存的 {@link Serializable} 对象
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Serializable)
     * @see #putOnNewThread(String, Serializable)
     * @see #putOnNewThread(String, Serializable, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Serializable value, long outTime) {
        if (value == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            if (outTime == -1) {
                return put(key, bytes);
            } else {
                return put(key, bytes, outTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(objectOutputStream);
        }
    }

    /**
     * 缓存字符串({@link String})内容
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param value 需要缓存的字符串内容({@link String})
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, String, long)
     * @see #putOnNewThread(String, String)
     * @see #putOnNewThread(String, String, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull String value) {
        if (TextUtils.isEmpty(value)) return null;

        File file = RCacheOperatorUtils.spliceFile(key);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(value);
            bufferedWriter.flush();

            // 检查缓存大小
            RCacheOperatorUtils.checkCacheSize();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(bufferedWriter);
        }
    }

    /**
     * 缓存字节数组(byte[])
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param bytes 需要缓存的字节数组(byte[])
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, byte[], long)
     * @see #putOnNewThread(String, byte[])
     * @see #putOnNewThread(String, byte[], long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;

        File file = RCacheOperatorUtils.spliceFile(key);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();

            // 检查缓存大小
            RCacheOperatorUtils.checkCacheSize();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(fileOutputStream);
        }
    }

    /**
     * 在新线程中缓存字符串({@link String})内容
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param value 需要缓存的字符串内容({@link String})
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, String)
     * @see #put(String, String, long)
     * @see #putOnNewThread(String, String, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull String value) {
        return putOnNewThread(key, value, -1);
    }

    /**
     * 在新线程中缓存字符串({@link String})内容，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param value   需要缓存的字符串({@link String})内容
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, String)
     * @see #put(String, String, long)
     * @see #putOnNewThread(String, String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final String value, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, value);
                else return put(key, value, outTime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link JSONObject} 对象
     *
     * @param key        缓存键名称，同时用于获取缓存
     * @param jsonObject 需要缓存的 {@link JSONObject} 对象
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONObject)
     * @see #put(String, JSONObject, long)
     * @see #putOnNewThread(String, JSONObject, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONObject jsonObject) {
        return putOnNewThread(key, jsonObject, -1);
    }

    /**
     * 在新线程中缓存 {@link JSONObject} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key        缓存键名称，同时用于获取缓存
     * @param jsonObject 需要缓存的 {@link JSONObject} 对象
     * @param outTime    有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONObject)
     * @see #put(String, JSONObject, long)
     * @see #putOnNewThread(String, JSONObject)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONObject jsonObject, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, jsonObject);
                else return put(key, jsonObject, outTime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link JSONArray} 对象
     *
     * @param key       缓存键名称，同时用于获取缓存
     * @param jsonArray 需要缓存的 {@link JSONArray} 对象
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONArray)
     * @see #put(String, JSONArray, long)
     * @see #putOnNewThread(String, JSONArray, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONArray jsonArray) {
        return putOnNewThread(key, jsonArray, -1);
    }

    /**
     * 在新线程中缓存 {@link JSONArray} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key       缓存键名称，同时用于获取缓存
     * @param jsonArray 需要缓存的 {@link JSONArray} 对象
     * @param outTime   有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONArray)
     * @see #put(String, JSONArray, long)
     * @see #putOnNewThread(String, JSONArray)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONArray jsonArray, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, jsonArray);
                else return put(key, jsonArray, outTime);
            }
        });
    }

    /**
     * 在新线程中缓存字节数组(byte[])
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param bytes 需要缓存的字节数组(byte[])
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, byte[])
     * @see #put(String, byte[], long)
     * @see #putOnNewThread(String, byte[], long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull byte[] bytes) {
        return putOnNewThread(key, bytes, -1);
    }

    /**
     * 在新线程中缓存字节数组(byte[])，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bytes   需要缓存的字节数组(byte[])
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, byte[])
     * @see #put(String, byte[], long)
     * @see #putOnNewThread(String, byte[])
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final byte[] bytes, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, bytes);
                else return put(key, bytes, outTime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link Bitmap} 对象
     *
     * @param key    缓存键名称，同时用于获取缓存
     * @param bitmap 需要缓存的  {@link Bitmap} 对象
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Bitmap)
     * @see #put(String, Bitmap, long)
     * @see #putOnNewThread(String, Bitmap, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Bitmap bitmap) {
        return putOnNewThread(key, bitmap, -1);
    }

    /**
     * 在新线程中缓存 {@link Bitmap} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bitmap  需要缓存的 {@link Bitmap} 对象
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Bitmap)
     * @see #put(String, Bitmap, long)
     * @see #putOnNewThread(String, Bitmap)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Bitmap bitmap, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, bitmap);
                else return put(key, bitmap, outTime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link Drawable} 对象
     *
     * @param key      缓存键名称，同时用于获取缓存
     * @param drawable 需要缓存的  {@link Drawable} 对象
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Drawable)
     * @see #put(String, Drawable, long)
     * @see #putOnNewThread(String, Drawable, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Drawable drawable) {
        return putOnNewThread(key, drawable, -1);
    }

    /**
     * 在新线程中缓存 {@link Drawable} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key      缓存键名称，同时用于获取缓存
     * @param drawable 需要缓存的 {@link Drawable} 对象
     * @param outTime  有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Drawable)
     * @see #put(String, Drawable, long)
     * @see #putOnNewThread(String, Drawable)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Drawable drawable, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, drawable);
                else return put(key, drawable, outTime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link Serializable} 对象
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param value 需要缓存的  {@link Serializable} 对象
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Serializable)
     * @see #put(String, Serializable, long)
     * @see #putOnNewThread(String, Serializable, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Serializable value) {
        return putOnNewThread(key, value, -1);
    }

    /**
     * 在新线程中缓存 {@link Serializable} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param value   需要缓存的 {@link Serializable} 对象
     * @param outTime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link RCacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Serializable)
     * @see #put(String, Serializable, long)
     * @see #putOnNewThread(String, Serializable)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Serializable value, final long outTime) {
        return RCacheThreadResult.<File>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outTime == -1) return put(key, value);
                else return put(key, value, outTime);
            }
        });
    }

    /**
     * 获取缓存的 {@link JSONObject} 对象，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的 {@link JSONObject} 对象，没有则返回 {@code null}
     * @see #getAsJsonObjectOnNewThread(String)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public JSONObject getAsJsonObject(@NonNull String key) {
        try {
            return new JSONObject(getAsString(key));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存的 {@link JSONArray} 对象，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的 {@link JSONArray} 对象，没有则返回 {@code null}
     * @see #getAsJSONArrayOnNewThread(String)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public JSONArray getAsJsonArray(@NonNull String key) {
        try {
            return new JSONArray(getAsString(key));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存的 {@link Bitmap} 对象，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的 {@link Bitmap} 对象，没有则返回 {@code null}
     * @see #getAsBitmapOnNewThread(String)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public Bitmap getAsBitmap(@NonNull String key) {
        byte[] bytes = getAsBinary(key);
        return RCacheOperatorUtils.bytesToBitmap(bytes);
    }

    /**
     * 获取缓存的 {@link Drawable} 对象，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的 {@link Drawable} 对象，没有则返回 {@code null}
     * @see #getAsDrawableOnNewThread(String)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public Drawable getAsDrawable(@NonNull String key) {
        Bitmap bitmap = getAsBitmap(key);
        return RCacheOperatorUtils.bitmapToDrawable(bitmap);
    }

    /**
     * 获取缓存的 {@link Serializable} 对象，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的 {@link Serializable} 对象，没有则返回 {@code null}
     * @see #getAsObject(String, Class)
     * @see #getAsObjectOnNewThread(String)
     * @see #getAsObjectOnNewThread(String, Class)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public Object getAsObject(@NonNull String key) {
        return getAsObject(key, Object.class);
    }

    /**
     * 获取缓存的 {@link Serializable} 对象，没有则返回 {@code null}
     *
     * @param key   缓存时的键名称
     * @param clazz 若知道该键名表示的哪个对象的实例，就可以指定
     * @param <T>   返回结果泛型
     * @return 缓存的 {@link Serializable} 对象，没有则返回 {@code null}
     * @see #getAsObject(String)
     * @see #getAsObjectOnNewThread(String)
     * @see #getAsObjectOnNewThread(String, Class)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public <T> T getAsObject(@NonNull String key, @NonNull Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("参数 clazz 不能为null");

        byte[] bytes = getAsBinary(key);
        if (bytes == null || bytes.length == 0) return null;

        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream = null;
        Object readObject;

        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            readObject = objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(objectInputStream);
        }

        if (readObject == null) return null;

        if (clazz.isAssignableFrom(readObject.getClass())) {
            return (T) readObject;
        } else {
            throw new ClassCastException("查询结果对象类型 " + readObject.getClass().getName() + " 不能转换成参数指定类型 " + clazz.getName());
        }
    }

    /**
     * 获取缓存的字符串内容({@link String})，没有则返回 {@code ""}
     *
     * @param key 缓存时的键名称
     * @return 缓存的字符串内容 ({@link String})，没有则返回 {@code ""}
     * @see #getAsStringOnNewThread(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public String getAsString(@NonNull String key) {
        File file = RCacheOperatorUtils.spliceFile(key);
        if (!file.exists()) return "";

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String tResult = stringBuilder.toString();
            // 是否包含有效期
            if (RCacheOperatorUtils.isTimeLimit(tResult)) {
                String resultValue = RCacheOperatorUtils.clearDateInfo(tResult);
                // 判断是否过期，如果已过期就删除该文件
                if (RCacheConfig.OUT_TIME_FLAG.equals(resultValue)) {
                    RCacheOperatorUtils.deleteFile(file);
                    return "";
                } else {
                    return resultValue;
                }
            }

            return tResult;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            IOUtils.close(bufferedReader);
        }
    }

    /**
     * 获取缓存的字节数组(byte[])，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的字节数组(byte[])，没有则返回 {@code null}
     * @see #getAsBinaryOnNewThread(String)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public byte[] getAsBinary(@NonNull String key) {
        File file = RCacheOperatorUtils.spliceFile(key);
        if (!file.exists()) return null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bys = new byte[1024 * 5];
            int len = 0;
            while ((len = fileInputStream.read(bys)) != -1) {
                outputStream.write(bys, 0, len);
            }
            byte[] readResult = outputStream.toByteArray();
            // 是否包含有效期
            if (RCacheOperatorUtils.isTimeLimit(readResult)) {
                byte[] resultValue = RCacheOperatorUtils.clearDateInfo(readResult);
                // 获取分隔符的字节形式
                byte[] outTimeBytes = RCacheOperatorUtils.toBytes(RCacheConfig.OUT_TIME_FLAG);
                // 判断是否过期，如果已过期就删除该文件
                if (RCacheOperatorUtils.equalsBytes(outTimeBytes, resultValue)) {
                    RCacheOperatorUtils.deleteFile(file);
                    return null;
                } else {
                    return resultValue;
                }
            }
            return readResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(fileInputStream);
        }
    }

    /**
     * 在新的线程获取缓存的字符串内容({@link String})
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsString(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<String> getAsStringOnNewThread(@NonNull final String key) {
        return RCacheThreadResult.<String>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<String>() {
            @Override
            public String execute() {
                return getAsString(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的 {@link JSONObject} 对象
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsJsonObject(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<JSONObject> getAsJsonObjectOnNewThread(@NonNull final String key) {
        return RCacheThreadResult.<JSONObject>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<JSONObject>() {
            @Override
            public JSONObject execute() {
                return getAsJsonObject(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的 {@link JSONArray} 对象
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsJsonArray(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<JSONArray> getAsJSONArrayOnNewThread(@NonNull final String key) {
        return RCacheThreadResult.<JSONArray>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<JSONArray>() {
            @Override
            public JSONArray execute() {
                return getAsJsonArray(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的字节数组(byte[])
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsBinary(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<byte[]> getAsBinaryOnNewThread(@NonNull final String key) {
        return RCacheThreadResult.<byte[]>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<byte[]>() {
            @Override
            public byte[] execute() {
                return getAsBinary(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的 {@link Bitmap} 对象
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsBitmap(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<Bitmap> getAsBitmapOnNewThread(@NonNull final String key) {
        return RCacheThreadResult.<Bitmap>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<Bitmap>() {
            @Override
            public Bitmap execute() {
                return getAsBitmap(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的 {@link Drawable} 对象
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsDrawable(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<Drawable> getAsDrawableOnNewThread(@NonNull final String key) {
        return RCacheThreadResult.<Drawable>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<Drawable>() {
            @Override
            public Drawable execute() {
                return getAsDrawable(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的 {@link Serializable} 对象
     *
     * @param key 缓存时的键名称
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsObject(String)
     * @see #getAsObject(String, Class)
     * @see #getAsObjectOnNewThread(String, Class)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public RCacheThreadResult<Object> getAsObjectOnNewThread(@NonNull final String key) {
        return getAsObjectOnNewThread(key, Object.class);
    }

    /**
     * 在新的线程获取缓存的 {@link Serializable} 对象
     *
     * @param key   缓存时的键名称
     * @param clazz 若知道该键名表示的哪个对象的实例，就可以指定
     * @param <T>   返回结果泛型
     * @return {@link RCacheThreadResult} 对象，内容为 {@link RCacheThreadResult#onResult(RCacheResultCallBack)} 回调方法参数
     * @see #getAsObject(String)
     * @see #getAsObject(String, Class)
     * @see #getAsObjectOnNewThread(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public <T> RCacheThreadResult<T> getAsObjectOnNewThread(@NonNull final String key, @NonNull final Class<T> clazz) {
        return RCacheThreadResult.<T>create().runOnNewThread(new RCacheThreadResult.CacheCallBack<T>() {
            @Override
            public T execute() {
                return getAsObject(key, clazz);
            }
        });
    }

    /**
     * 删除指定缓存，注意： 在主线程删除
     *
     * @param key 缓存时的键名称
     * @see #clearOnNewThread(String)
     */
    public void clear(@NonNull String key) {
        File file = RCacheOperatorUtils.spliceFile(key);
        if (file.exists())
            RCacheOperatorUtils.deleteFile(file);
    }

    /**
     * 删除指定缓存 子线程操作，不会阻塞线程
     *
     * @param key 缓存时的键名称
     * @see #clear(String)
     */
    public void clearOnNewThread(@NonNull final String key) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                File file = RCacheOperatorUtils.spliceFile(key);
                if (file.exists())
                    RCacheOperatorUtils.deleteFile(file);
            }
        };
        RCacheConfig.EXECUTORSERVICE.execute(runnable);
    }

    /**
     * 清除所有缓存数据 子线程操作，不会阻塞线程
     */
    public void clearCache() {
        RCacheOperatorUtils.clearCache();
    }
}
