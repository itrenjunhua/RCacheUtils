package cache.renj.com.cacheutils.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    @org.jetbrains.annotations.Contract(value = "null -> null")
    public static JSONObject string2JsonObject(@NonNull String jsonString) {
        if (StringUtils.isEmpty(jsonString)) return null;

        if (!jsonString.startsWith("{") || !jsonString.endsWith("}")) return null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject;
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
    @org.jetbrains.annotations.Contract(value = "null -> null")
    public static String jsonObject2String(@NonNull JSONObject jsonObject) {
        if (jsonObject == null) return null;

        String jsonString = jsonObject.toString();
        return jsonString;
    }
}
