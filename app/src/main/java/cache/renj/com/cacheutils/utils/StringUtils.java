package cache.renj.com.cacheutils.utils;

import android.support.annotation.CheckResult;
import android.text.TextUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-05   14:52
 * <p>
 * 描述：操作字符串相关的类
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class StringUtils {
    /**
     * 判断单个字符串是否为空
     *
     * @param string
     * @return true：空 false：非空
     */
    @CheckResult(suggest = "返回值没有被使用过")
    @org.jetbrains.annotations.Contract("null -> true")
    public static boolean isEmpty(String string) {
        if (TextUtils.isEmpty(string) || "null" == string)
            return true;
        return false;
    }

    /**
     * 判断单多个字符串是否为空
     *
     * @param strings
     * @return true：空 false：非空
     */
    @CheckResult(suggest = "返回值没有被使用过")
    @org.jetbrains.annotations.Contract("null -> true")
    public static boolean isEmpty(String... strings) {
        if (strings == null) return true;

        for (String string : strings) {
            if (TextUtils.isEmpty(string) || "null" == string)
                return true;
        }
        return false;
    }
}
