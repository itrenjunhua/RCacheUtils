package com.renj.cachelibrary;

import android.support.annotation.NonNull;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-01-30   10:03
 * <p>
 * 描述：缓存管理线程辅助工具类。主要作用：1.用于切换线程；2.在子线程获取到结果后提供回调
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public final class CacheThreadResult<T> {

    private T execute;
    private CacheResultCallBack<? super T> cacheResultCallBack;

    private CacheThreadResult() {
    }

    /**
     * 创建新的 {@link CacheThreadResult} 对象
     *
     * @param <T> 泛型
     * @return {@link CacheThreadResult} 对象
     */
    @NonNull
    static <T> CacheThreadResult<T> create() {
        return new CacheThreadResult<>();
    }

    /**
     * 需要运行在新的线程的代码
     *
     * @param cacheCallBack {@link CacheCallBack<T>} 接口回调方法中的代码就是运行在新的线程的方法
     * @return {@link CacheThreadResult} 对象
     */
    CacheThreadResult<T> runOnNewThread(final CacheCallBack<T> cacheCallBack) {
        RCacheConfig.EXECUTORSERVICE.execute(new Runnable() {
            @Override
            public void run() {
                execute = cacheCallBack.execute();
                if (cacheResultCallBack != null)
                    returnMainThread(cacheResultCallBack);
            }
        });
        return this;
    }

    /**
     * 切换回主线程运行，通过回调返回 {@link #runOnNewThread(CacheCallBack)} 方法的结果
     */
    private void returnMainThread(@NonNull final CacheResultCallBack<? super T> cacheResultCallBack) {
        RCacheConfig.MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cacheResultCallBack.onResult(execute);
            }
        });
    }

    /**
     * 得到结果，方法运行在主线程
     *
     * @param cacheResultCallBack 回调，具体的内容作为回调方法的参数
     */
    public void onResult(@NonNull CacheResultCallBack<? super T> cacheResultCallBack) {
        this.cacheResultCallBack = cacheResultCallBack;
    }

    /**
     * 将需要在新线程中执行的代码编入回调方法中
     *
     * @param <T>
     */
    interface CacheCallBack<T> {
        T execute();
    }
}
