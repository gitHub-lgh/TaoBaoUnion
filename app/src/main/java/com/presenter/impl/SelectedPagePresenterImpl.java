package com.presenter.impl;

import com.model.API;
import com.model.domain.SelectedContent;
import com.model.domain.SelectedPageCategory;
import com.presenter.ISelectedPagePresenter;
import com.utils.LogUtils;
import com.utils.RetrofitManager;
import com.utils.UrlUtils;
import com.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {

    private final API api;

    public SelectedPagePresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        api = retrofit.create(API.class);
    }

    private ISelectedPageCallback mViewCallback = null;

    @Override
    public void getCategories() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        Call<SelectedPageCategory> task = api.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                int code = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this,"result code --> "+code);
                if(code == HttpURLConnection.HTTP_OK){
                    //请求成功
                    SelectedPageCategory result = response.body();
                    //通知UI更新
                    mViewCallback.onCategoriesLoaded(result);
                }else{
                    //请求失败
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable t) {
                //失败
                onLoadedError();
            }
        });
    }

    private void onLoadedError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
    }

    @Override
    public void getContentByCategory(SelectedPageCategory.DataBean item) {
        int categoryId = item.getFavorites_id();
        LogUtils.d(this,"categoryId --> " + categoryId);
        String targetUrl = UrlUtils.getSelectedPageContentUrl(categoryId);
        LogUtils.d(this,"targetUrl --> " + targetUrl);
        Call<SelectedContent> task = api.getSelectedContent(targetUrl);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                int code = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this,"result code --> "+code);
                if(code == HttpURLConnection.HTTP_OK) {
                    SelectedContent result = response.body();
                    if(mViewCallback != null){
                        mViewCallback.onContentLoad(result);
                    }
                }else{
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = null;
    }
}
