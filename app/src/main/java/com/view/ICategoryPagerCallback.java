package com.view;

import com.base.IBaseCallback;
import com.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallback extends IBaseCallback {
    /**
     * 数据加载回来
     */
    void onContentLoaded(List<HomePagerContent.DataBean> contents);

    int getCategoryId();


    /**
     * 加载更多网络出错
     */
    void onLoadMoreError();

    /**
     * 没有更多内容
     */
    void onLoadMoreEmpty();

    /**
     * 成功加载更多内容
     * @param contents
     */
    void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents);

    /**
     * 加载轮播图
     * @param contents
     */
    void onLooperListLoaded(List<HomePagerContent.DataBean> contents);

}
