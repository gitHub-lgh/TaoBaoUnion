package com.view;

import com.base.IBaseCallback;
import com.model.domain.TicketResult;

public interface ITicketPagerCallback extends IBaseCallback {
    /**
     * 淘口令加载结果
     * @param cover
     * @param result
     */
    void onTicketLoaded(String cover, TicketResult result);
}
