package cache.renj.com.cacheutils.test;

import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.renj.cachelibrary.CacheManageUtils;
import com.renj.cachelibrary.CacheThreadResult;

import org.json.JSONArray;
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
    @BindView(R.id.tv_get_content)
    TextView tvGetContent;
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
                ViewUtils.showView(tvGetContent);
                ViewUtils.goneView(ivGetContent);
                etGetKey.setText("cache_string");
                break;
            case JSON_OBJECT:
                setTitle(ResUtils.getString(R.string.get_jsonobject));
                ViewUtils.showView(tvGetContent);
                ViewUtils.goneView(ivGetContent);
                etGetKey.setText("cache_jsonobject");
                break;
            case JSON_ARRAY:
                setTitle(ResUtils.getString(R.string.get_jsonarray));
                ViewUtils.showView(tvGetContent);
                ViewUtils.goneView(ivGetContent);
                etGetKey.setText("cache_jsonarray");
                break;
            case BYTE:
                setTitle(ResUtils.getString(R.string.get_byte));
                ViewUtils.showView(tvGetContent);
                ViewUtils.goneView(ivGetContent);
                etGetKey.setText("cache_bytes");
                break;
            case OBJECT:
                setTitle(ResUtils.getString(R.string.get_object));
                ViewUtils.showView(tvGetContent);
                ViewUtils.goneView(ivGetContent);
                etGetKey.setText("cache_object");
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
            if (!judgeCacheKey())
                return;

            switch (dataType) {
                case STRING:
                    getStringData();
                    break;
                case JSON_OBJECT:
                    getJsonObjectData();
                    break;
                case JSON_ARRAY:
                    getJsonArrayData();
                    break;
                case BYTE:
                    getBytesData();
                    break;
                case OBJECT:
                    getObjectData();
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
     * 根据文件名获取缓存的 {@link Object} 数据
     */
    private void getObjectData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsObjectOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<Object>() {
                        @Override
                        public void onResult(Object result) {
                            if (result != null)
                                tvGetContent.setText(result + "");
                            else
                                tvGetContent.setText("");
                        }
                    });
        } else {
            Object result = CacheManageUtils.newInstance()
                    .getAsObject(getEditTextContetnt(etGetKey));
            if (result != null)
                tvGetContent.setText(result + "");
            else
                tvGetContent.setText("");
        }
    }

    /**
     * 根据文件名获取缓存的 {@link Byte}[] 数据
     */
    private void getBytesData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsBinaryOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<byte[]>() {
                        @Override
                        public void onResult(byte[] result) {
                            if (result != null)
                                tvGetContent.setText(new String(result) + "");
                            else
                                tvGetContent.setText("");
                        }
                    });
        } else {
            byte[] result = CacheManageUtils.newInstance()
                    .getAsBinary(getEditTextContetnt(etGetKey));
            if (result != null)
                tvGetContent.setText(new String(result) + "");
            else
                tvGetContent.setText("");
        }
    }

    /**
     * 根据文件名获取缓存的 {@link JSONArray} 数据
     */
    private void getJsonArrayData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsJSONArrayOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<JSONArray>() {
                        @Override
                        public void onResult(JSONArray result) {
                            tvGetContent.setText(JsonUtils.jsonArray2String(result) + "");
                        }
                    });
        } else {
            JSONArray result = CacheManageUtils.newInstance()
                    .getAsJsonArray(getEditTextContetnt(etGetKey));
            tvGetContent.setText(JsonUtils.jsonArray2String(result) + "");
        }
    }

    /**
     * 根据文件名获取缓存的 {@link JSONObject} 数据
     */
    private void getJsonObjectData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsJsonObjectOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<JSONObject>() {
                        @Override
                        public void onResult(JSONObject result) {
                            tvGetContent.setText(JsonUtils.jsonObject2String(result) + "");
                        }
                    });
        } else {
            JSONObject result = CacheManageUtils.newInstance()
                    .getAsJsonObjct(getEditTextContetnt(etGetKey));
            tvGetContent.setText(JsonUtils.jsonObject2String(result) + "");
        }
    }

    /**
     * 根据文件名获取缓存的 {@link String} 数据
     */
    private void getStringData() {
        if (isNewThread()) {
            // 需要在新的线程中
            CacheManageUtils.newInstance()
                    .getAsStringOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheThreadResult.CacheResultCallBack<String>() {
                        @Override
                        public void onResult(String result) {
                            tvGetContent.setText(result);
                        }
                    });
        } else {
            String result = CacheManageUtils.newInstance()
                    .getAsString(getEditTextContetnt(etGetKey));
            tvGetContent.setText(result);
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
