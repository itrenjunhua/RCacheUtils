package cache.renj.com.cacheutils.utils;

import android.support.annotation.NonNull;
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
     * 获取字符串
     *
     * @param stringId
     * @return
     */
    @NonNull
    public static String getString(@StringRes int stringId) {
        return UIUtils.getContext().getResources().getString(stringId);
    }
}
