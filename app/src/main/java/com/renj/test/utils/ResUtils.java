package com.renj.test.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-05   11:02
 * <p>
 * 描述：操作资源文件的工具类
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class ResUtils {
    /**
     * 获取 {@link Resources} 对象
     *
     * @return
     */
    @NonNull
    public static Resources getResources() {
        return UIUtils.getContext().getResources();
    }

    /**
     * 获取字符串
     *
     * @param stringId
     * @return
     */
    @NonNull
    public static String getString(@StringRes int stringId) {
        return UIUtils.getContext().getResources().getString(stringId);
    }

    /**
     * 获取drawable-xx/bitmap-xx文件夹下的图片文件变为 {@link Drawable}
     *
     * @param drawableId
     * @return
     */
    @Nullable
    public static Drawable getDrawable(@DrawableRes int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getResources().getDrawable(drawableId, null);
        else
            return getResources().getDrawable(drawableId);
    }

    /**
     * 获取drawable-xx/bitmap-xx文件夹下的图片文件变为 {@link Bitmap}
     *
     * @param drawableId
     * @return
     */
    @Nullable
    public static Bitmap getBitmap(@DrawableRes int drawableId) {
        Drawable drawable = getDrawable(drawableId);
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
}
