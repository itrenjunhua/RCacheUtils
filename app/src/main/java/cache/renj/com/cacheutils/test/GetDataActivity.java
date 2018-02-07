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

import org.json.JSONObject;

import butterknife.BindView;
import cache.renj.com.cacheutils.BaseActivity;
import cache.renj.com.cacheutils.R;
import cache.renj.com.cacheutils.utils.JsonUtils;
import cache.renj.com.cacheutils.utils.ResUtils;
import cache.renj.com.cacheutils.utils.StringUtils;
import cache.renj.com.cacheutils.utils.UIUtils;
import cache.renj.com.cacheutils.utils.ViewUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-04   17:33
 * <p>
 * 描述：获取缓存数据页面
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class GetDataActivity extends BaseActivity {
    @BindView(R.id.et_get_content)
    EditText etGetContent;
    @BindView(R.id.iv_get_content)
    ImageView ivGetContent;
    @BindView(R.id.et_get_key)
    EditText etGetKey;
    @BindView(R.id.cb_get_thread)
    CheckBox cbGetThread;
    @BindView(R.id.bt_get_data)
    Button btGetData;

    private CacheDataType dataType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_get;
    }

    @Override
    protected void initData() {
        initIntent();
        isShowBack(true);

        btGetData.setOnClickListener(this);
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
     * @param dataType
     */
    private void initDataByDataType(CacheDataType dataType) {
        switch (dataType) {
            case STRING:
                setTitle(ResUtils.getString(R.string.get_string));
                ViewUtils.showView(etGetContent);
                ViewUtils.goneView(ivGetContent);
                etGetKey.setText("cache_string");
                break;
            case JSON_OBJECT:
                setTitle(ResUtils.getString(R.string.get_jsonobject));
                break;
            case JSON_ARRAY:
                setTitle(ResUtils.getString(R.string.get_jsonarray));
                break;
            case BYTE:
                setTitle(ResUtils.getString(R.string.get_byte));
                break;
            case OBJECT:
                setTitle(ResUtils.getString(R.string.get_object));
                break;
            case BITMAP:
                setTitle(ResUtils.getString(R.string.get_bitmap));
                break;
            case DRAWABLE:
                setTitle(ResUtils.getString(R.string.get_drawable));
                break;
        }
    }

    @Override
    protected void handClick(int vId) {
        if (R.id.bt_get_data == vId) {
            switch (dataType) {
                case STRING:
                    if (judgeCacheKey())
                        getStringData();
                    break;
                case JSON_OBJECT:
                    if (judgeCacheKey())
                        getJsonObjectData();
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
     * 根据输入和选择内容缓存 {@link JSONObject}
     */
    private void getJsonObjectData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsJsonObjectOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<JSONObject>() {
                        @Override
                        public void onResult(JSONObject result) {
                            etGetContent.setText(JsonUtils.jsonObject2String(result) + "");
                        }
                    });
        } else {
            JSONObject result = CacheManageUtils.newInstance()
                    .getAsJsonObjct(getEditTextContetnt(etGetKey));
            etGetContent.setText(JsonUtils.jsonObject2String(result) + "");
        }
    }

    /**
     * 根据输入和选择内容缓存字符串
     */
    private void getStringData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsStringOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<String>() {
                        @Override
                        public void onResult(String result) {
                            etGetContent.setText(result);
                        }
                    });
        } else {
            String result = CacheManageUtils.newInstance()
                    .getAsString(getEditTextContetnt(etGetKey));
            etGetContent.setText(result);
        }
    }

    /**
     * 判断是否需要在新的线程中执行
     *
     * @return true：是 false：不是
     */
    @CheckResult(suggest = "返回值没有被使用过")
    private boolean isNewThread() {
        return cbGetThread.isChecked();
    }

    /**
     * 判断是否输入了缓存文件名和缓存内容
     *
     * @return true：输入了 false：没有输入
     */
    @NonNull
    @CheckResult(suggest = "返回值没有被使用过")
    private boolean judgeCacheKey() {
        String key = getEditTextContetnt(etGetKey);
        if (StringUtils.isEmpty(key)) {
            UIUtils.showToastSafe("请输入缓存文件名");
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
