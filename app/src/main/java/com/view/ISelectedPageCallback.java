package com.view;

import com.base.IBaseCallback;
import com.model.domain.SelectedContent;
import com.model.domain.SelectedContent3;
import com.model.domain.SelectedPageCategory;

public interface ISelectedPageCallback extends IBaseCallback {

    /**
     * 分类
     */
    void onCategoriesLoaded(SelectedPageCategory categories);

    /**
     * 内容
     * @param content
     */
    void onContentLoad(SelectedContent content);

}
