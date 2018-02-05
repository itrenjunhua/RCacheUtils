package cache.renj.com.cacheutils.test;

import android.content.Intent;

import cache.renj.com.cacheutils.BaseActivity;
import cache.renj.com.cacheutils.R;
import cache.renj.com.cacheutils.utils.ResUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-04   17:21
 * <p>
 * 描述：缓存数据的Activity
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class CacheDataActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_cache;
    }

    @Override
    protected void initData() {
        initIntent();
        isShowBack(true);
    }

    private void initIntent() {
        Intent intent = getIntent();
        String dataType = intent.getStringExtra("dataType");
        if (dataType != null) {
            initDataByDataType(CacheDataType.valueOf(dataType));
        }
    }

    /**
     * 根据数据类型初始化数据
     *
     * @param dataType
     */
    private void initDataByDataType(CacheDataType dataType) {
        switch (dataType) {
            case STRING:
                setTitle(ResUtils.getString(R.string.cache_string));
                break;
            case JSON_OBJECT:
                setTitle(ResUtils.getString(R.string.cache_jsonobject));
                break;
            case JSON_ARRAY:
                setTitle(ResUtils.getString(R.string.cache_jsonarray));
                break;
            case BYTE:
                setTitle(ResUtils.getString(R.string.cache_byte));
                break;
            case OBJECT:
                setTitle(ResUtils.getString(R.string.cache_object));
                break;
            case BITMAP:
                setTitle(ResUtils.getString(R.string.cache_bitmap));
                break;
            case DRAWABLE:
                setTitle(ResUtils.getString(R.string.cache_drawable));
                break;
        }
    }
}
