package cache.renj.com.cacheutils.test;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-05   11:31
 * <p>
 * 描述：缓存数据类型枚举
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public enum CacheDataType {
    STRING("STRING"), JSON_OBJECT("JSON_OBJECT"), JSON_ARRAY("JSON_ARRAY"),
    BYTE("BYTE"), OBJECT("OBJECT"), BITMAP("BITMAP"), DRAWABLE("DRAWABLE");

    private String value;

    CacheDataType(String value) {
        this.value = value;
    }

    @org.jetbrains.annotations.Contract(pure = true)
    public String value() {
        return value;
    }
}
