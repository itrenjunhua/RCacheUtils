package cache.renj.com.cacheutils;

import android.widget.Button;

import butterknife.BindView;

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
        switch (vId) {
        }
    }
}
