package cache.renj.com.cacheutils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-02   15:11
 * <p>
 * 描述：Activity 基类
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class BaseActivity extends AppCompatActivity {
    protected static BaseActivity foregroundActivity;

    @BindView(R.id.title_view_back)
    TextView titleViewBack;
    @BindView(R.id.title_view_title)
    TextView titleViewTitle;
    @BindView(R.id.rl_title_view)
    RelativeLayout rlTitleView;
    @BindView(R.id.fl_main_content)
    FrameLayout flMainContent;

    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        bind = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @org.jetbrains.annotations.Contract(pure = true)
    public static BaseActivity getForegroundActivity() {
        return foregroundActivity;
    }
}
