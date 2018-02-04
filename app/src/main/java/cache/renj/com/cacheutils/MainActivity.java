package cache.renj.com.cacheutils;

import android.content.Intent;
import android.widget.Button;

import butterknife.BindView;
import cache.renj.com.cacheutils.test.CacheDataActivity;
import cache.renj.com.cacheutils.test.GetDataActivity;

public class MainActivity extends BaseActivity {
    @BindView(R.id.bt_cahe_data)
    Button btCaheData;
    @BindView(R.id.bt_get_data)
    Button btGetData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        isShowBack(false);
        setTitle("首页");

        btCaheData.setOnClickListener(this);
        btGetData.setOnClickListener(this);
    }

    @Override
    protected void handClick(int vId) {
        Intent intent;
        switch (vId) {
            case R.id.bt_cahe_data:
                intent = new Intent(this, CacheDataActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_get_data:
                intent = new Intent(this, GetDataActivity.class);
                startActivity(intent);
                break;
        }
    }
}
