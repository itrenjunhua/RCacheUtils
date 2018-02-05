package cache.renj.com.cacheutils.test;

import android.content.Intent;
import android.os.Bundle;

import cache.renj.com.cacheutils.BaseActivity;
import cache.renj.com.cacheutils.R;

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
        Bundle extras = intent.getExtras();
        if (extras != null) {
            setTitle(extras.getString("title"));
        }
    }
}
