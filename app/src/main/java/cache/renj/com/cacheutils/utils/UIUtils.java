package cache.renj.com.cacheutils.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import cache.renj.com.cacheutils.MyApplication;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-02   15:58
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class UIUtils {
    /**
     * 获取全局的上下文
     *
     * @return 全局的上下文
     */
    @org.jetbrains.annotations.Contract(pure = true)
    public static MyApplication getContext() {
        return MyApplication.application;
    }

    /**
     * dip转换成px
     *
     * @param dip
     * @return px值
     */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换成dip
     *
     * @param px
     * @return dp值
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 获取主线程的{@link Handler}
     *
     * @return 主线程的Handler
     */
    @org.jetbrains.annotations.Contract(pure = true)
    public static Handler getHandler() {
        return getContext().getMainHandler();
    }

    /**
     * 获取主线程的 {@link Looper}
     *
     * @return 主线程的 Looper
     */
    public static Looper getMainLooper() {
        return getContext().getMainLooper();
    }

    /**
     * 获取主线对象
     *
     * @return 主线程 Thread
     */
    public static Thread getMainThread() {
        return getContext().getMainThread();
    }

    /**
     * 判断当前的线程是不是在主线程
     *
     * @return true：是主线程
     */
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThread().getId();
    }

    /**
     * 延时在主线程执行{@link Runnable}
     *
     * @param runnable    需要执行的 {@link Runnable}
     * @param delayMillis 延迟时间
     * @return 是否执行成功 true：成功
     */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在主线程执行{@link Runnable}
     *
     * @param runnable 需要执行的 {@link Runnable}
     * @return 是否执行成功 true：成功
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 从主线程looper里面移除{@link Runnable}
     *
     * @param runnable 需要移出的 {@link Runnable}
     */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param str 现实的信息
     */
    public static void showToastSafe(final String str) {
        if (isRunInMainThread()) {
            showToast(str);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showToast(str);
                }
            });
        }
    }

    private static Toast mToast;

    /**
     * 显示单例Toast
     *
     * @param str
     */
    private static void showToast(String str) {
            if (null == mToast)
                mToast = Toast.makeText(UIUtils.getContext(), str, Toast.LENGTH_LONG);
            mToast.setText(str);
            mToast.show();
    }
}
