package cache.renj.com.cacheutils.test;

import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.renj.cachelibrary.CacheManageUtils;
import com.renj.cachelibrary.CacheThreadResult;

import java.io.File;

import butterknife.BindView;
import cache.renj.com.cacheutils.BaseActivity;
import cache.renj.com.cacheutils.R;
import cache.renj.com.cacheutils.utils.ResUtils;
import cache.renj.com.cacheutils.utils.StringUtils;
import cache.renj.com.cacheutils.utils.UIUtils;
import cache.renj.com.cacheutils.utils.ViewUtils;

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
    @BindView(R.id.et_cache_content)
    EditText etCacheContent;
    @BindView(R.id.iv_cache_content)
    ImageView ivCacheContent;
    @BindView(R.id.et_cache_time)
    EditText etCacheTime;
    @BindView(R.id.et_cache_key)
    EditText etCacheKey;
    @BindView(R.id.cb_cache_thread)
    CheckBox cbCacheThread;
    @BindView(R.id.bt_cache_data)
    Button btCacheData;

    private CacheDataType dataType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cache;
    }

    @Override
    protected void initData() {
        initIntent();
        isShowBack(true);

        btCacheData.setOnClickListener(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        String dataTypeString = intent.getStringExtra("dataType");
        if (dataTypeString != null) {
            dataType = CacheDataType.valueOf(dataTypeString);
            initDataByDataType(dataType);
        }
    }

    /**
     * 根据数据类型初始化数据
     *
     * @param dataType 数据类型
     */
    private void initDataByDataType(CacheDataType dataType) {
        switch (dataType) {
            case STRING:
                setTitle(ResUtils.getString(R.string.cache_string));
                ViewUtils.showView(etCacheContent);
                ViewUtils.goneView(ivCacheContent);
                etCacheContent.setText("缓存字符串数据的默认具体内容");
                etCacheKey.setText("cache_string");
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

    @Override
    protected void handClick(int vId) {
        if (R.id.bt_cache_data == vId) {
            switch (dataType) {
                case STRING:
                    if (judgeCacheKeyAndContent())
                        cacheStringData();
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

    /**
     * 根据输入和选择内容缓存字符串
     */
    private void cacheStringData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent), cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("子线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent), cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent))
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("子线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent));
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }

    }

    /**
     * 判断是否需要在新的线程中执行
     *
     * @return true：是 false：不是
     */
    @CheckResult(suggest = "返回值没有被使用过")
    private boolean isNewThread() {
        return cbCacheThread.isChecked();
    }

    /**
     * 判断是否输入的缓存时间限制
     *
     * @return 输入的缓存时间限制
     */
    @CheckResult(suggest = "返回值没有被使用过")
    private int getCacheTime() {
        try {
            String timeContent = etCacheTime.getText().toString().trim();
            if (StringUtils.isEmpty(timeContent)) return 0;
            return Integer.parseInt(timeContent);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 判断是否输入了缓存文件名和缓存内容
     *
     * @return true：输入了 false：没有输入
     */
    @NonNull
    @CheckResult(suggest = "返回值没有被使用过")
    private boolean judgeCacheKeyAndContent() {
        String key = getEditTextContetnt(etCacheKey);
        if (StringUtils.isEmpty(key)) {
            UIUtils.showToastSafe("请输入缓存文件名");
            return false;
        }
        String contetnt = getEditTextContetnt(etCacheContent);
        if (StringUtils.isEmpty(contetnt)) {
            UIUtils.showToastSafe("请输入缓存内容");
            return false;
        }
        return true;
    }

    /**
     * 获取EditText的内容
     *
     * @return EditText的内容
     */
    @NonNull
    @CheckResult(suggest = "返回值没有被使用过")
    private String getEditTextContetnt(@NonNull EditText editText) {
        return editText.getText().toString().trim();
    }
}
