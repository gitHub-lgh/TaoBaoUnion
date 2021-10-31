package com.presenter.impl;

import com.model.API;
import com.model.domain.OnSellContent;
import com.presenter.IOnsellPagerPresenter;
import com.utils.LogUtils;
import com.utils.RetrofitManager;
import com.utils.UrlUtils;
import com.view.IOnSellPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagerPresenterImpl implements IOnsellPagerPresenter {
    public static final int DEFAULT_PAGE = 1;

    private int currentPage = DEFAULT_PAGE;
    private IOnSellPagerCallback mOnSellPageCallback = null;
    private API mApi;

    public OnSellPagerPresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(API.class);
    }

    @Override
    public void getOnSellContent() {
        if(mIsLoading){
            return;
        }
        mIsLoading = true;
        //加载中。。。
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onLoading();
        }
        //获取特惠内容
        String targetUrl = UrlUtils.getOnSellPageUrl(currentPage);
        Call<OnSellContent> task = mApi.getOnSellContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(this,"reslut code ---> " + code);
                if(code == HttpURLConnection.HTTP_OK){
                    OnSellContent result = response.body();
                    onSuccess(result);
                }else{
                    onError();
                }

            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onError();
            }
        });
    }


    private void onEmpty(){
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onEmpty();
        }
    }

    private void onError() {
        mIsLoading = false;
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onError();
        }
    }

    private void onSuccess(OnSellContent result) {
        if (mOnSellPageCallback != null) {
            try{
                if(isEmpty(result)){
                    onEmpty();
                }else{
                    mOnSellPageCallback.onContentLoadedSuccess(result);
                }
            }catch (Exception e){
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    private boolean isEmpty(OnSellContent result){
        int size = result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size == 0;
    }
    @Override
    public void reLoad() {
        this.getOnSellContent();
    }

    /**
     * 当前加载状态,用来防止多次调用加载
     */
    private boolean mIsLoading = false;

    @Override
    public void loaderMore() {
        if(mIsLoading){
            return;
        }
        mIsLoading = true;
        //页码增加
        currentPage++;
        //加载更多内容
        String targetUrl = UrlUtils.getOnSellPageUrl(currentPage);
        Call<OnSellContent> task = mApi.getOnSellContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(this,"reslut code ---> " + code);
                if(code == HttpURLConnection.HTTP_OK){
                    OnSellContent result = response.body();
                    onMoreLoaded(result);
                }else{
                    onMoreError();
                }
            }
            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onMoreError();
            }
        });
    }

    private void onMoreError() {
        mIsLoading = false;
        currentPage--;
        mOnSellPageCallback.onMoreLoadedError();
    }

    /**
     * 加载更多内容结果，通知UI更新
     * @param result
     */
    private void onMoreLoaded(OnSellContent result) {
        if(mOnSellPageCallback != null){
            try{
                if(isEmpty(result)){
                    currentPage--;
                    mOnSellPageCallback.onMoreLoadedEmpty();
                }else{
                    mOnSellPageCallback.onMoreLoaded(result);
                }
            }catch (Exception e){
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    @Override
    public void registerViewCallback(IOnSellPagerCallback callback) {
        this.mOnSellPageCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellPagerCallback callback) {
        this.mOnSellPageCallback = null;
    }
}
