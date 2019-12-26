package com.renj.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.renj.cache.RCacheManageUtils;
import com.renj.cache.RCacheResultCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;

import com.renj.test.base.BaseActivity;
import com.renj.test.bean.CacheDataType;
import com.renj.test.bean.Person;
import com.renj.test.utils.JsonUtils;
import com.renj.test.utils.ResUtils;
import com.renj.test.utils.StringUtils;
import com.renj.test.utils.UIUtils;
import com.renj.test.utils.ViewUtils;

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
                ViewUtils.showView(ivGetContent);
                ViewUtils.goneView(tvGetContent);
                etGetKey.setText("cache_bitmap");
                break;
            case DRAWABLE:
                setTitle(ResUtils.getString(R.string.get_drawable));
                ViewUtils.showView(ivGetContent);
                ViewUtils.goneView(tvGetContent);
                etGetKey.setText("cache_drawable");
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
                    getBitmapData();
                    break;
                case DRAWABLE:
                    getDrawableData();
                    break;
            }
        }
    }

    /**
     * 根据文件名获取缓存的 {@link Drawable} 数据
     */
    private void getDrawableData() {
        if (isNewThread()) {
            // 需要在新的线程中
            RCacheManageUtils.getInstance()
                    .getAsDrawableOnNewThread(getEditTextContent(etGetKey))
                    .onResult(new RCacheResultCallBack<Drawable>() {
                        @Override
                        public void onResult(Drawable result) {
                            ivGetContent.setImageDrawable(result);
                        }
                    });
        } else {
            Drawable result = RCacheManageUtils.getInstance()
                    .getAsDrawable(getEditTextContent(etGetKey));
            ivGetContent.setImageDrawable(result);
        }
    }

    /**
     * 根据文件名获取缓存的 {@link Bitmap} 数据
     */
    private void getBitmapData() {
        if (isNewThread()) {
            // 需要在新的线程中
            RCacheManageUtils.getInstance()
                    .getAsBitmapOnNewThread(getEditTextContent(etGetKey))
                    .onResult(new RCacheResultCallBack<Bitmap>() {
                        @Override
                        public void onResult(Bitmap result) {
                            ivGetContent.setImageBitmap(result);
                        }
                    });
        } else {
            Bitmap result = RCacheManageUtils.getInstance()
                    .getAsBitmap(getEditTextContent(etGetKey));
            ivGetContent.setImageBitmap(result);
        }
    }


    /**
     * 根据文件名获取缓存的 {@link Object} 数据
     */
    private void getObjectData() {
        if (isNewThread()) {
            // 需要在新的线程中
            RCacheManageUtils.getInstance()
                    .getAsObjectOnNewThread(getEditTextContent(etGetKey), Person.class)
                    .onResult(new RCacheResultCallBack<Person>() {
                        @Override
                        public void onResult(Person result) {
                            if (result != null)
                                tvGetContent.setText(result + "");
                            else
                                tvGetContent.setText("");
                        }
                    });
        } else {
            Object result = RCacheManageUtils.getInstance()
                    .getAsObject(getEditTextContent(etGetKey));
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
            RCacheManageUtils.getInstance()
                    .getAsBinaryOnNewThread(getEditTextContent(etGetKey))
                    .onResult(new RCacheResultCallBack<byte[]>() {
                        @Override
                        public void onResult(byte[] result) {
                            if (result != null)
                                tvGetContent.setText(new String(result) + "");
                            else
                                tvGetContent.setText("");
                        }
                    });
        } else {
            byte[] result = RCacheManageUtils.getInstance()
                    .getAsBinary(getEditTextContent(etGetKey));
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
            RCacheManageUtils.getInstance()
                    .getAsJSONArrayOnNewThread(getEditTextContent(etGetKey))
                    .onResult(new RCacheResultCallBack<JSONArray>() {
                        @Override
                        public void onResult(JSONArray result) {
                            tvGetContent.setText(JsonUtils.jsonArray2String(result) + "");
                        }
                    });
        } else {
            JSONArray result = RCacheManageUtils.getInstance()
                    .getAsJsonArray(getEditTextContent(etGetKey));
            tvGetContent.setText(JsonUtils.jsonArray2String(result) + "");
        }
    }

    /**
     * 根据文件名获取缓存的 {@link JSONObject} 数据
     */
    private void getJsonObjectData() {
        if (isNewThread()) {
            // 需要在新的线程中
            RCacheManageUtils.getInstance()
                    .getAsJsonObjectOnNewThread(getEditTextContent(etGetKey))
                    .onResult(new RCacheResultCallBack<JSONObject>() {
                        @Override
                        public void onResult(JSONObject result) {
                            tvGetContent.setText(JsonUtils.jsonObject2String(result) + "");
                        }
                    });
        } else {
            JSONObject result = RCacheManageUtils.getInstance()
                    .getAsJsonObject(getEditTextContent(etGetKey));
            tvGetContent.setText(JsonUtils.jsonObject2String(result) + "");
        }
    }

    /**
     * 根据文件名获取缓存的 {@link String} 数据
     */
    private void getStringData() {
        if (isNewThread()) {
            // 需要在新的线程中
            RCacheManageUtils.getInstance()
                    .getAsStringOnNewThread(getEditTextContent(etGetKey))
                    .onResult(new RCacheResultCallBack<String>() {
                        @Override
                        public void onResult(String result) {
                            tvGetContent.setText(result);
                        }
                    });
        } else {
            String result = RCacheManageUtils.getInstance()
                    .getAsString(getEditTextContent(etGetKey));
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
        String key = getEditTextContent(etGetKey);
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
    private String getEditTextContent(@NonNull EditText editText) {
        return editText.getText().toString().trim();
    }
}
