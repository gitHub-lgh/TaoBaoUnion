package com.view;

import com.base.IBaseCallback;
import com.model.domain.OnSellContent;

public interface IOnSellPagerCallback extends IBaseCallback {
    
    /**
     * 特惠内容加载完成
     */
    void onContentLoadedSuccess(OnSellContent result);

    /**
     * 加载更多的内容
     */
    void onMoreLoaded(OnSellContent moreResult);

    /**
     * 加载更多时失败
     */
    void onMoreLoadedError();

    /**
     * 没有更多内容
     */
    void onMoreLoadedEmpty();
}
