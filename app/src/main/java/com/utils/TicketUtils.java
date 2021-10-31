package com.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.model.domain.IBaseInfo;
import com.presenter.ITicketPresenter;
import com.ui.activity.TicketActivity;

public class TicketUtils {

    public static void toTicketPage(Context context,IBaseInfo baseInfo){
        //特惠列表内容被点击
        //处理数据
        String title = baseInfo.getTitle();
        //详情地址/优惠卷地址
        String url = baseInfo.getUrl();
        if(TextUtils.isEmpty(url)){
            url = baseInfo.getUrl();
        }
        String cover = baseInfo.getCover();
        //拿到ticketPresenter去加载数据
        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getmTicketPresenter();
        ticketPresenter.getTicket(title,url,cover);
        context.startActivity(new Intent(context, TicketActivity.class));
    }
}
