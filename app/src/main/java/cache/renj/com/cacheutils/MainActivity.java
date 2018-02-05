package cache.renj.com.cacheutils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import cache.renj.com.cacheutils.test.CacheDataActivity;
import cache.renj.com.cacheutils.test.GetDataActivity;
import cache.renj.com.cacheutils.utils.ResUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bt_cache_string)
    Button btCacheString;
    @BindView(R.id.bt_get_string)
    Button btGetString;
    @BindView(R.id.bt_cache_jsonobject)
    Button btCacheJsonobject;
    @BindView(R.id.bt_get_jsonobject)
    Button btGetJsonobject;
    @BindView(R.id.bt_cache_jsonarray)
    Button btCacheJsonarray;
    @BindView(R.id.bt_get_jsonarray)
    Button btGetJsonarray;
    @BindView(R.id.bt_cache_byte)
    Button btCacheByte;
    @BindView(R.id.bt_get_byte)
    Button btGetByte;
    @BindView(R.id.bt_cache_object)
    Button btCacheObject;
    @BindView(R.id.bt_get_object)
    Button btGetObject;
    @BindView(R.id.bt_cache_bitmap)
    Button btCacheBitmap;
    @BindView(R.id.bt_get_bitmap)
    Button btGetBitmap;
    @BindView(R.id.bt_cache_drawable)
    Button btCacheDrawable;
    @BindView(R.id.bt_get_drawable)
    Button btGetDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        isShowBack(false);
        setTitle(ResUtils.getString(R.string.home_page_title));

        setListener();
    }

    /**
     * 设置按钮控件监听器
     */
    private void setListener() {
        btCacheString.setOnClickListener(this);
        btGetString.setOnClickListener(this);
        btCacheJsonobject.setOnClickListener(this);
        btGetJsonobject.setOnClickListener(this);
        btCacheJsonarray.setOnClickListener(this);
        btGetJsonarray.setOnClickListener(this);
        btCacheByte.setOnClickListener(this);
        btGetByte.setOnClickListener(this);
        btCacheObject.setOnClickListener(this);
        btGetObject.setOnClickListener(this);
        btCacheBitmap.setOnClickListener(this);
        btGetBitmap.setOnClickListener(this);
        btCacheDrawable.setOnClickListener(this);
        btGetDrawable.setOnClickListener(this);
    }

    @Override
    protected void handClick(int vId) {
        Intent intent = null;
        Bundle extras = null;
        switch (vId) {
            case R.id.bt_cache_string:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_string));
                break;
            case R.id.bt_cache_jsonobject:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_jsonobject));
                break;
            case R.id.bt_cache_jsonarray:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_jsonarray));
                break;
            case R.id.bt_cache_byte:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_byte));
                break;
            case R.id.bt_cache_object:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_object));
                break;
            case R.id.bt_cache_bitmap:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_bitmap));
                break;
            case R.id.bt_cache_drawable:
                intent = new Intent(this, CacheDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.cache_drawable));
                break;
            case R.id.bt_get_string:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_string));
                break;
            case R.id.bt_get_jsonobject:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_jsonobject));
                break;
            case R.id.bt_get_jsonarray:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_jsonarray));
                break;
            case R.id.bt_get_byte:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_byte));
                break;
            case R.id.bt_get_object:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_object));
                break;
            case R.id.bt_get_bitmap:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_bitmap));
                break;
            case R.id.bt_get_drawable:
                intent = new Intent(this, GetDataActivity.class);
                extras = new Bundle();
                extras.putString("title",ResUtils.getString(R.string.get_drawable));
                break;
        }

        if (intent != null) {
            if (extras != null)
                intent.putExtras(extras);

            startActivity(intent);
        }
    }
}
