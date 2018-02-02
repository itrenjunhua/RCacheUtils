package cache.renj.com.cacheutils.utils;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-02   17:00
 * <p>
 * 描述：操作View的工具类
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class ViewUtils {
    /**
     * 显示多个控件
     *
     * @param views 控件，可变参数
     */
    public static void showView(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * INVISIBLE 多个控件
     *
     * @param views 控件，可变参数
     */
    public static void invilibleView(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * GONE 多个控件
     *
     * @param views 控件，可变参数
     */
    public static void goneView(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 把自身从父View中移除
     *
     * @param view 需要移出的View
     */
    public static void removeSelfFromParent(@NonNull View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
        }
    }

    /**
     * 请求View树重新布局，用于解决中层View有布局状态而导致上层View状态断裂
     *
     * @param view  需要重新布局的View
     * @param isAll 是否将所有上级控件进行重新布局 true：是
     */
    public static void requestLayoutParent(@NonNull View view, @NonNull boolean isAll) {
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof View) {
            if (!parent.isLayoutRequested()) {
                parent.requestLayout();
                if (!isAll) {
                    break;
                }
            }
            parent = parent.getParent();
        }
    }

    /**
     * 判断触摸事件是否落在该View上
     *
     * @param ev MotionEvent 对象
     * @param v  需要判断的View
     * @return true：在该View上 false：没有在该View上
     */
    public static boolean isTouchInView(@NonNull MotionEvent ev, @NonNull View v) {
        int[] vLoc = new int[2];
        v.getLocationOnScreen(vLoc);
        float motionX = ev.getRawX();
        float motionY = ev.getRawY();
        return motionX >= vLoc[0] && motionX <=
                (vLoc[0] + v.getWidth()) && motionY >= vLoc[1] && motionY <= (vLoc[1] + v.getHeight());
    }

    /**
     * findViewById的泛型封装，减少强转代码
     *
     * @param layout 根布局对象
     * @param id     控件id
     * @param <T>    T extends View
     * @return id对应的控件
     */
    public static <T extends View> T findViewById(@NonNull View layout, @NonNull int id) {
        return (T) layout.findViewById(id);
    }
}
