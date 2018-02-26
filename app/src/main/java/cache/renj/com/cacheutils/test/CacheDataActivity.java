package cache.renj.com.cacheutils.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.renj.cachelibrary.CacheManageUtils;
import com.renj.cachelibrary.CacheThreadResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

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
    private Person person;
    private Bitmap bitmap;
    private Drawable drawable;

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
                etCacheContent.setText("缓存字符串数据的默认内容：aaabbbccdddAAABBBCCCDDD");
                etCacheKey.setText("cache_string");
                break;
            case JSON_OBJECT:
                setTitle(ResUtils.getString(R.string.cache_jsonobject));
                ViewUtils.showView(etCacheContent);
                ViewUtils.goneView(ivCacheContent);
                etCacheContent.setText("{\"name\":\"张三\",\"age\":25,\"sex\":\"男\"}");
                etCacheKey.setText("cache_jsonobject");
                break;
            case JSON_ARRAY:
                setTitle(ResUtils.getString(R.string.cache_jsonarray));
                ViewUtils.showView(etCacheContent);
                ViewUtils.goneView(ivCacheContent);
                String jsonArayString = "[{\"name\":\"张三\",\"age\":25,\"sex\":\"男\"}," +
                        "{\"name\":\"李四\",\"age\":23,\"sex\":\"男\"}]";
                etCacheContent.setText(jsonArayString);
                etCacheKey.setText("cache_jsonarray");
                break;
            case BYTE:
                setTitle(ResUtils.getString(R.string.cache_byte));
                ViewUtils.showView(etCacheContent);
                ViewUtils.goneView(ivCacheContent);
                String bytesString = "缓存byte数据的默认内容：aaabbbcccddd（将变为byte[]缓存起来）";
                etCacheContent.setText(bytesString);
                etCacheKey.setText("cache_bytes");
                break;
            case OBJECT:
                setTitle(ResUtils.getString(R.string.cache_object));
                ViewUtils.showView(etCacheContent);
                ViewUtils.goneView(ivCacheContent);
                etCacheContent.setFocusable(false);
                person = new Person("张三", 25, '男');
                etCacheContent.setText("Person 对象 => " + person.toString());
                etCacheKey.setText("cache_object");
                break;
            case BITMAP:
                setTitle(ResUtils.getString(R.string.cache_bitmap));
                ViewUtils.showView(ivCacheContent);
                ViewUtils.goneView(etCacheContent);
                bitmap = ResUtils.getBitmap(R.mipmap.ic_launcher);
                if (bitmap != null)
                    ivCacheContent.setImageBitmap(bitmap);
                etCacheKey.setText("cache_bitmap");
                break;
            case DRAWABLE:
                setTitle(ResUtils.getString(R.string.cache_drawable));
                ViewUtils.showView(ivCacheContent);
                ViewUtils.goneView(etCacheContent);
                drawable = ResUtils.getDrawable(R.mipmap.ic_launcher_round);
                if (drawable != null)
                    ivCacheContent.setImageDrawable(drawable);
                etCacheKey.setText("cache_drawable");
                break;
        }
    }

    @Override
    protected void handClick(int vId) {
        if (R.id.bt_cache_data == vId) {
            if (!judgeCacheKeyAndContent())
                return;
            switch (dataType) {
                case STRING:
                    cacheStringData();
                    break;
                case JSON_OBJECT:
                    cacheJsonObjectData();
                    break;
                case JSON_ARRAY:
                    cacheJsonArrayData();
                    break;
                case BYTE:
                    cacheBytesData();
                    break;
                case OBJECT:
                    cacheObjectData();
                    break;
                case BITMAP:
                    cacheBitmapData();
                    break;
                case DRAWABLE:
                    cacheDrawableData();
                    break;
            }
        }
    }

    /**
     * 缓存 {@link Drawable} 类型数据
     */
    private void cacheDrawableData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), drawable, cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), drawable, cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), drawable)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), drawable);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }
    }

    /**
     * 缓存 {@link Bitmap} 类型数据
     */
    private void cacheBitmapData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), bitmap, cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), bitmap, cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), bitmap)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), bitmap);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }
    }

    /**
     * 缓存 {@link Object} 类型数据
     */
    private void cacheObjectData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), person, cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), person, cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), person)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), person);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }
    }

    /**
     * 根据输入和选择内容缓存 {@link Byte}[] 数据
     */
    private void cacheBytesData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        String contetnt = getEditTextContetnt(etCacheContent);
        byte[] bytes = contetnt.getBytes();
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), bytes, cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), bytes, cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), bytes)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), bytes);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }
    }

    /**
     * 根据输入和选择内容缓存 {@link JSONArray} 数据
     */
    private void cacheJsonArrayData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        String contetnt = getEditTextContetnt(etCacheContent);
        JSONArray jsonArray = JsonUtils.string2JsonArray(contetnt);
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), jsonArray, cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), jsonArray, cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), jsonArray)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), jsonArray);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }
    }

    /**
     * 根据输入和选择内容缓存 {@link JSONObject} 数据
     */
    private void cacheJsonObjectData() {
        int cacheTime = getCacheTime();
        boolean isNewThread = isNewThread();
        String contetnt = getEditTextContetnt(etCacheContent);
        JSONObject jsonObject = JsonUtils.string2JsonObject(contetnt);
        if (cacheTime > 0) {
            // 有时间限制
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), jsonObject, cacheTime)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), jsonObject, cacheTime);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        } else {
            if (isNewThread) {
                // 需要在新的线程中
                CacheManageUtils.newInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey), jsonObject)
                        .onResult(new CacheThreadResult.CacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
            } else {
                File result = CacheManageUtils.newInstance()
                        .put(getEditTextContetnt(etCacheKey), jsonObject);
                UIUtils.showToastSafe("缓存文件位置 => " + result);
            }
        }
    }

    /**
     * 根据输入和选择内容缓存 {@link String} 数据
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
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
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
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
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

    /**********************************************************/

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
    @CheckResult(suggest = "返回值没有被使用过")
    private boolean judgeCacheKeyAndContent() {
        String key = getEditTextContetnt(etCacheKey);
        if (StringUtils.isEmpty(key)) {
            UIUtils.showToastSafe("请输入缓存文件名");
            return false;
        }
        if (CacheDataType.BITMAP != dataType && CacheDataType.DRAWABLE != dataType) {
            String contetnt = getEditTextContetnt(etCacheContent);
            if (StringUtils.isEmpty(contetnt)) {
                UIUtils.showToastSafe("请输入缓存内容");
                return false;
            }
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
