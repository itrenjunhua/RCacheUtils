package com.renj.cachelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    public File put(@NonNull String key, @NonNull String value, @NonNull long outtime) {
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
    public File put(@NonNull String key, @NonNull JSONObject jsonObject, @NonNull long outtime) {
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
    public File put(@NonNull String key, @NonNull JSONArray jsonArray, @NonNull long outtime) {
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
    public File put(@NonNull String key, @NonNull byte[] bytes, @NonNull long outtime) {
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
    public File put(@NonNull String key, @NonNull Bitmap bitmap, @NonNull long outtime) {
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
    public File put(@NonNull String key, @NonNull Drawable drawable, @NonNull long outtime) {
        return put(key, RCacheOperatorUtils.drawableToBitmap(drawable), outtime);
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
     * 获取缓存的 {@link JSONObject} 对象，没有则返回 {@code null}
     *
     * @param key 缓存时的键名称
     * @return 缓存的 {@link JSONObject} 对象，没有则返回 {@code null}
     * @see #getAsJsonObjectOnNewThread(String)
     */
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
    public Drawable getAsDrawable(@NonNull String key) {
        Bitmap bitmap = getAsBitmap(key);
        return RCacheOperatorUtils.bitmapToDrawable(bitmap);
    }

    /**
     * 获取缓存的字符串内容({@link String})，没有则返回 {@code ""}
     *
     * @param key 缓存时的键名称
     * @return 缓存的字符串内容 ({@link String})，没有则返回 {@code ""}
     * @see #getAsStringOnNewThread(String)
     */
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
}
