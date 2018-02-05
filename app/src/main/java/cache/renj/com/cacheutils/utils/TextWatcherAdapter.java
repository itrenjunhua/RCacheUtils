package cache.renj.com.cacheutils.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-05   14:51
 * <p>
 * 描述：TextWatcher 适配器类，减少方法重写数
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
