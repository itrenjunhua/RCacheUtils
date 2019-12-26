package com.renj.cache;

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
public final class RCacheThreadResult<T> {

    private T execute;
    private RCacheResultCallBack<? super T> rCacheResultCallBack;

    private RCacheThreadResult() {
    }

    /**
     * 创建新的 {@link RCacheThreadResult} 对象
     *
     * @param <T> 泛型
     * @return {@link RCacheThreadResult} 对象
     */
    @NonNull
    static <T> RCacheThreadResult<T> create() {
        return new RCacheThreadResult<>();
    }

    /**
     * 需要运行在新的线程的代码
     *
     * @param cacheCallBack {@link CacheCallBack<T>} 接口回调方法中的代码就是运行在新的线程的方法
     * @return {@link RCacheThreadResult} 对象
     */
    RCacheThreadResult<T> runOnNewThread(final CacheCallBack<T> cacheCallBack) {
        RCacheConfig.EXECUTORSERVICE.execute(new Runnable() {
            @Override
            public void run() {
                execute = cacheCallBack.execute();
                if (rCacheResultCallBack != null)
                    returnMainThread(rCacheResultCallBack);
            }
        });
        return this;
    }

    /**
     * 切换回主线程运行，通过回调返回 {@link #runOnNewThread(CacheCallBack)} 方法的结果
     */
    private void returnMainThread(@NonNull final RCacheResultCallBack<? super T> rCacheResultCallBack) {
        RCacheConfig.MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                rCacheResultCallBack.onResult(execute);
            }
        });
    }

    /**
     * 得到结果，方法运行在主线程
     *
     * @param rCacheResultCallBack 回调，具体的内容作为回调方法的参数
     */
    public void onResult(@NonNull RCacheResultCallBack<? super T> rCacheResultCallBack) {
        this.rCacheResultCallBack = rCacheResultCallBack;
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
