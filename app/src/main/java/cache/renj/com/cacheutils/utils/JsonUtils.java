package cache.renj.com.cacheutils.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-07   9:44
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class JsonUtils {
    /**
     * json 字符串变为 {@link JSONObject}
     *
     * @param jsonString json 字符串
     * @return 转变后的 {@link JSONObject}
     */
    @Nullable
    @SuppressWarnings("unused")
    @CheckResult(suggest = "结果从未使用过")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    public static JSONObject string2JsonObject(@NonNull String jsonString) {
        if (StringUtils.isEmpty(jsonString)) return null;

        if (!jsonString.startsWith("{") || !jsonString.endsWith("}")) return null;

        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@link JSONObject} 变为 json 字符串
     *
     * @param jsonObject {@link JSONObject}
     * @return 转变后的 json 字符串
     */
    @Nullable
    @SuppressWarnings("unused")
    @CheckResult(suggest = "结果从未使用过")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    public static String jsonObject2String(@NonNull JSONObject jsonObject) {
        if (jsonObject == null) return null;

        return jsonObject.toString();
    }

    /**
     * json 字符串变为 {@link JSONArray}
     *
     * @param jsonString json 字符串
     * @return 转变后的 {@link JSONArray}
     */
    @Nullable
    @SuppressWarnings("unused")
    @CheckResult(suggest = "结果从未使用过")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    public static JSONArray string2JsonArray(@NonNull String jsonString) {
        if (StringUtils.isEmpty(jsonString)) return null;

        if (!jsonString.startsWith("[") || !jsonString.endsWith("]")) return null;

        try {
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@link JSONArray} 变为 json 字符串
     *
     * @param jsonArray {@link JSONArray}
     * @return 转变后的 json 字符串
     */
    @Nullable
    @SuppressWarnings("unused")
    @CheckResult(suggest = "结果从未使用过")
    @org.jetbrains.annotations.Contract(value = "null -> null")
    public static String jsonArray2String(@NonNull JSONArray jsonArray) {
        if (jsonArray == null) return null;

        return jsonArray.toString();
    }
}
