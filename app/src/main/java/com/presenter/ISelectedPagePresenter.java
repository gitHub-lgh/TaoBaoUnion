package com.presenter;

import com.base.IBasePresenter;
import com.model.domain.SelectedPageCategory;
import com.view.ISelectedPageCallback;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {
    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 根据分类获取分类内容
     */
    void getContentByCategory(SelectedPageCategory.DataBean item);

    /**
     * 重新加载内容
     */
    void reloadContent();
}
