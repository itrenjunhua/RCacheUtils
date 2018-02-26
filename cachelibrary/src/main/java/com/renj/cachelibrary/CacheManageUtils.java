package com.renj.cachelibrary;

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
public final class CacheManageUtils {
    /**
     * 缓存路径
     */
    static File CACHE_PATH;
    /**
     * 缓存大小，默认 {@link RCacheConfig#CACHE_SIZE}
     */
    static long caheSize = RCacheConfig.CACHE_SIZE;
    /**
     * 缓存大小检查和删除文件线程
     */
    static volatile RCacheSizeControl RCACHE_SIZE_CONTROL;
    /**
     * 每秒的毫秒数
     */
    private static final long SECOND = 1000;
    /**
     * CacheManageUtils 实例对象
     */
    private static CacheManageUtils instance;

    private CacheManageUtils(Context context, String fileName, long caheSize) {
        CACHE_PATH = new File(context.getCacheDir(), fileName);
        if (!CACHE_PATH.exists() || !CACHE_PATH.isDirectory())
            CACHE_PATH.mkdir();

        RCACHE_SIZE_CONTROL = new RCacheSizeControl();
        CacheManageUtils.caheSize = caheSize;
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context 上下文
     */
    @SuppressWarnings("unused")
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
        initCacheUtil(context, fileName, RCacheConfig.CACHE_SIZE);
    }

    /**
     * 初始化缓存管理工具类，在使用该缓存管理工具类前必须先调用 {@link #initCacheUtil(Context)} 方法(使用默认缓存目录名'ACache')
     * 或者 {@link #initCacheUtil(Context, String)} 方法(可以指定缓存目录名)进行初始化，建议在 Application 中调用。
     *
     * @param context  上下文
     * @param fileName 缓存目录的名称
     * @param caheSize 缓存总大小
     */
    public static void initCacheUtil(@NonNull Context context, @NonNull String fileName, long caheSize) {
        if (caheSize < 0) {
            throw new IllegalArgumentException("参数 caheSize 的值必须大于0.");
        }

        if (instance == null) {
            synchronized (CacheManageUtils.class) {
                if (instance == null) {
                    instance = new CacheManageUtils(context, fileName, caheSize);
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
    @CheckResult(suggest = "返回值没有使用")
    public static CacheManageUtils newInstance() {
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
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, String)
     * @see #putOnNewThread(String, String)
     * @see #putOnNewThread(String, String, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull String value, long outtime) {
        return put(key, RCacheOperatorUtils.addDateInfo(value, outtime * SECOND));
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
     * @param outtime    有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, JSONObject)
     * @see #putOnNewThread(String, JSONObject)
     * @see #putOnNewThread(String, JSONObject, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull JSONObject jsonObject, long outtime) {
        return put(key, RCacheOperatorUtils.addDateInfo(jsonObject.toString(), outtime * SECOND));
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
     * @param outtime   有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, JSONArray)
     * @see #putOnNewThread(String, JSONArray)
     * @see #putOnNewThread(String, JSONArray, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull JSONArray jsonArray, long outtime) {
        return put(key, RCacheOperatorUtils.addDateInfo(jsonArray.toString(), outtime * SECOND));
    }

    /**
     * 缓存字节数组(byte[])，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bytes   需要缓存的字节数组(byte[])
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, byte[])
     * @see #putOnNewThread(String, byte[])
     * @see #putOnNewThread(String, byte[], long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull byte[] bytes, long outtime) {
        return put(key, RCacheOperatorUtils.addDateInfo(bytes, outtime * SECOND));
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
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Bitmap)
     * @see #putOnNewThread(String, Bitmap)
     * @see #putOnNewThread(String, Bitmap, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Bitmap bitmap, long outtime) {
        return put(key, RCacheOperatorUtils.addDateInfo(RCacheOperatorUtils.bitmapToBytes(bitmap), outtime * SECOND));
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
     * @param outtime  有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Drawable)
     * @see #putOnNewThread(String, Drawable)
     * @see #putOnNewThread(String, Drawable, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Drawable drawable, long outtime) {
        return put(key, RCacheOperatorUtils.drawableToBitmap(drawable), outtime);
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
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return 保存的文件 {@link File} 对象 <b>注意：可能为 {@code null}</b>
     * @see #put(String, Serializable)
     * @see #putOnNewThread(String, Serializable)
     * @see #putOnNewThread(String, Serializable, long)
     */
    @Nullable
    @CheckResult(suggest = "返回值没有使用")
    public File put(@NonNull String key, @NonNull Serializable value, long outtime) {
        if (value == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            if (outtime == -1) {
                return put(key, bytes);
            } else {
                return put(key, bytes, outtime);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在新线程中缓存字符串({@link String})内容
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param value 需要缓存的字符串内容({@link String})
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, String)
     * @see #put(String, String, long)
     * @see #putOnNewThread(String, String, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull String value) {
        return putOnNewThread(key, value, -1);
    }

    /**
     * 在新线程中缓存字符串({@link String})内容，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param value   需要缓存的字符串({@link String})内容
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, String)
     * @see #put(String, String, long)
     * @see #putOnNewThread(String, String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final String value, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, value);
                else return put(key, value, outtime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link JSONObject} 对象
     *
     * @param key        缓存键名称，同时用于获取缓存
     * @param jsonObject 需要缓存的 {@link JSONObject} 对象
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONObject)
     * @see #put(String, JSONObject, long)
     * @see #putOnNewThread(String, JSONObject, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONObject jsonObject) {
        return putOnNewThread(key, jsonObject, -1);
    }

    /**
     * 在新线程中缓存 {@link JSONObject} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key        缓存键名称，同时用于获取缓存
     * @param jsonObject 需要缓存的 {@link JSONObject} 对象
     * @param outtime    有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONObject)
     * @see #put(String, JSONObject, long)
     * @see #putOnNewThread(String, JSONObject)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONObject jsonObject, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, jsonObject);
                else return put(key, jsonObject, outtime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link JSONArray} 对象
     *
     * @param key       缓存键名称，同时用于获取缓存
     * @param jsonArray 需要缓存的 {@link JSONArray} 对象
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONArray)
     * @see #put(String, JSONArray, long)
     * @see #putOnNewThread(String, JSONArray, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONArray jsonArray) {
        return putOnNewThread(key, jsonArray, -1);
    }

    /**
     * 在新线程中缓存 {@link JSONArray} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key       缓存键名称，同时用于获取缓存
     * @param jsonArray 需要缓存的 {@link JSONArray} 对象
     * @param outtime   有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, JSONArray)
     * @see #put(String, JSONArray, long)
     * @see #putOnNewThread(String, JSONArray)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONArray jsonArray, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, jsonArray);
                else return put(key, jsonArray, outtime);
            }
        });
    }

    /**
     * 在新线程中缓存字节数组(byte[])
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param bytes 需要缓存的字节数组(byte[])
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, byte[])
     * @see #put(String, byte[], long)
     * @see #putOnNewThread(String, byte[], long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull byte[] bytes) {
        return putOnNewThread(key, bytes, -1);
    }

    /**
     * 在新线程中缓存字节数组(byte[])，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bytes   需要缓存的字节数组(byte[])
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, byte[])
     * @see #put(String, byte[], long)
     * @see #putOnNewThread(String, byte[])
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final byte[] bytes, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, bytes);
                else return put(key, bytes, outtime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link Bitmap} 对象
     *
     * @param key    缓存键名称，同时用于获取缓存
     * @param bitmap 需要缓存的  {@link Bitmap} 对象
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Bitmap)
     * @see #put(String, Bitmap, long)
     * @see #putOnNewThread(String, Bitmap, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Bitmap bitmap) {
        return putOnNewThread(key, bitmap, -1);
    }

    /**
     * 在新线程中缓存 {@link Bitmap} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param bitmap  需要缓存的 {@link Bitmap} 对象
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Bitmap)
     * @see #put(String, Bitmap, long)
     * @see #putOnNewThread(String, Bitmap)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Bitmap bitmap, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, bitmap);
                else return put(key, bitmap, outtime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link Drawable} 对象
     *
     * @param key      缓存键名称，同时用于获取缓存
     * @param drawable 需要缓存的  {@link Drawable} 对象
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Drawable)
     * @see #put(String, Drawable, long)
     * @see #putOnNewThread(String, Drawable, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Drawable drawable) {
        return putOnNewThread(key, drawable, -1);
    }

    /**
     * 在新线程中缓存 {@link Drawable} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key      缓存键名称，同时用于获取缓存
     * @param drawable 需要缓存的 {@link Drawable} 对象
     * @param outtime  有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Drawable)
     * @see #put(String, Drawable, long)
     * @see #putOnNewThread(String, Drawable)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Drawable drawable, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, drawable);
                else return put(key, drawable, outtime);
            }
        });
    }

    /**
     * 在新线程中缓存 {@link Serializable} 对象
     *
     * @param key   缓存键名称，同时用于获取缓存
     * @param value 需要缓存的  {@link Serializable} 对象
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Serializable)
     * @see #put(String, Serializable, long)
     * @see #putOnNewThread(String, Serializable, long)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Serializable value) {
        return putOnNewThread(key, value, -1);
    }

    /**
     * 在新线程中缓存 {@link Serializable} 对象，指定缓存时间，<b>注意：缓存时间单位为 秒(S)</b>
     *
     * @param key     缓存键名称，同时用于获取缓存
     * @param value   需要缓存的 {@link Serializable} 对象
     * @param outtime 有效时间，<b>注意：缓存时间单位为 秒(S)</b>
     * @return {@link CacheThreadResult} 对象，回调函数参数表示缓存的文件 {@link File} 对象，可能为 {@code null}
     * @see #put(String, Serializable)
     * @see #put(String, Serializable, long)
     * @see #putOnNewThread(String, Serializable)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Serializable value, final long outtime) {
        return CacheThreadResult.<File>create().runOnNewThread(new CacheThreadResult.CacheCallBack<File>() {
            @Override
            public File execute() {
                if (outtime == -1) return put(key, value);
                else return put(key, value, outtime);
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
    public JSONObject getAsJsonObjct(@NonNull String key) {
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
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
                String resultVaule = RCacheOperatorUtils.clearDateInfo(tResult);
                // 判断是否过期，如果已过期就删除该文件
                if (RCacheConfig.OUT_TIME_FLAG.equals(resultVaule)) {
                    RCacheOperatorUtils.deleteFile(file);
                    return "";
                } else {
                    return resultVaule;
                }
            }

            return tResult;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在新的线程获取缓存的字符串内容({@link String})
     *
     * @param key 缓存时的键名称
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsString(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<String> getAsStringOnNewThread(@NonNull final String key) {
        return CacheThreadResult.<String>create().runOnNewThread(new CacheThreadResult.CacheCallBack<String>() {
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
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsJsonObjct(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<JSONObject> getAsJsonObjectOnNewThread(@NonNull final String key) {
        return CacheThreadResult.<JSONObject>create().runOnNewThread(new CacheThreadResult.CacheCallBack<JSONObject>() {
            @Override
            public JSONObject execute() {
                return getAsJsonObjct(key);
            }
        });
    }

    /**
     * 在新的线程获取缓存的 {@link JSONArray} 对象
     *
     * @param key 缓存时的键名称
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsJsonArray(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<JSONArray> getAsJSONArrayOnNewThread(@NonNull final String key) {
        return CacheThreadResult.<JSONArray>create().runOnNewThread(new CacheThreadResult.CacheCallBack<JSONArray>() {
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
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsBinary(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<byte[]> getAsBinaryOnNewThread(@NonNull final String key) {
        return CacheThreadResult.<byte[]>create().runOnNewThread(new CacheThreadResult.CacheCallBack<byte[]>() {
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
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsBitmap(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<Bitmap> getAsBitmapOnNewThread(@NonNull final String key) {
        return CacheThreadResult.<Bitmap>create().runOnNewThread(new CacheThreadResult.CacheCallBack<Bitmap>() {
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
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsDrawable(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<Drawable> getAsDrawableOnNewThread(@NonNull final String key) {
        return CacheThreadResult.<Drawable>create().runOnNewThread(new CacheThreadResult.CacheCallBack<Drawable>() {
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
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsObject(String)
     * @see #getAsObject(String, Class)
     * @see #getAsObjectOnNewThread(String, Class)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public CacheThreadResult<Object> getAsObjectOnNewThread(@NonNull final String key) {
        return getAsObjectOnNewThread(key, Object.class);
    }

    /**
     * 在新的线程获取缓存的 {@link Serializable} 对象
     *
     * @param key   缓存时的键名称
     * @param clazz 若知道该键名表示的哪个对象的实例，就可以指定
     * @param <T>   返回结果泛型
     * @return {@link CacheThreadResult} 对象，内容为 {@link CacheThreadResult#onResult(CacheThreadResult.CacheResultCallBack)} 回调方法参数
     * @see #getAsObject(String)
     * @see #getAsObject(String, Class)
     * @see #getAsObjectOnNewThread(String)
     */
    @NonNull
    @CheckResult(suggest = "返回值没有使用")
    public <T> CacheThreadResult<T> getAsObjectOnNewThread(@NonNull final String key, @NonNull final Class<T> clazz) {
        return CacheThreadResult.<T>create().runOnNewThread(new CacheThreadResult.CacheCallBack<T>() {
            @Override
            public T execute() {
                return getAsObject(key, clazz);
            }
        });
    }
}
