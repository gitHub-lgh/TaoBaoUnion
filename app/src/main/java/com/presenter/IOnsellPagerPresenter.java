package com.presenter;

import com.base.IBasePresenter;
import com.view.IOnSellPagerCallback;

public interface IOnsellPagerPresenter extends IBasePresenter<IOnSellPagerCallback> {
    /**
     * 加载特惠内容
     */
    void getOnSellContent();

    /**
     * 重新加载内容
     */
    void reLoad();

    /**
     * 加载更多特惠内容
     */
    void loaderMore();
}
