package com.presenter.impl;

import com.model.API;
import com.model.domain.TicketParams;
import com.model.domain.TicketResult;
import com.presenter.ITicketPresenter;
import com.utils.LogUtils;
import com.utils.RetrofitManager;
import com.utils.UrlUtils;
import com.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    private ITicketPagerCallback mViewCallback = null;
    private TicketResult result;
    private String mCover;

    enum LoadState{
        LOADING,SUCCESS,ERROR,NONE
    }
    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {
        this.mCover = cover;

        this.onTicketLoading();
        LogUtils.e(TicketPresenterImpl.this, "title ---->" + title);
        LogUtils.e(TicketPresenterImpl.this, "url ---->" + url);
        LogUtils.e(TicketPresenterImpl.this, "cover ---->" + cover);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        API api = retrofit.create(API.class);
        // 给url添加前缀 https
        String ticketUrl = UrlUtils.getTicketUrl(url);
        //LogUtils.e(TicketPresenterImpl.this, "ticketUrl ---->" + ticketUrl);
        TicketParams ticketParams = new TicketParams(ticketUrl,title);
        Call<TicketResult> task = api.getTicket(ticketParams);
//        LogUtils.e(TicketPresenterImpl.this, "title ---->" + ticketParams.getTitle());
//        LogUtils.e(TicketPresenterImpl.this, "ticketUrl ---->" + ticketParams.getUrl());
//        LogUtils.e(TicketPresenterImpl.this, "task ---->" + task.toString());
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.e(TicketPresenterImpl.this, "code ---->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    result = response.body();
                    LogUtils.e(TicketPresenterImpl.this, "result ---->" + response.body().getMessage());
                    //通知UI更新
                    onTicketLoadSuccess();
                }else{
                    //请求失败
                    onLoadedTicketError();
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                //失败
                onLoadedTicketError();
            }
        });
    }

    private void onTicketLoadSuccess() {
        if(mViewCallback != null){
            mViewCallback.onTicketLoaded(mCover, result);
        }else{
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadedTicketError() {

        if(mViewCallback != null){
            mViewCallback.onError();
        }else{
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = callback;
        if(mCurrentState != LoadState.NONE){
            //说明状态已经改变
            //更新UI
            if(mCurrentState == LoadState.SUCCESS){
                onTicketLoadSuccess();
            }else if(mCurrentState == LoadState.ERROR){
                onLoadedTicketError();
            }else if(mCurrentState == LoadState.LOADING){
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }else{
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = null;
    }
}