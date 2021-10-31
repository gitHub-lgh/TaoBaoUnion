package com.presenter.impl;

import android.util.Log;

import com.model.API;
import com.model.domain.Categories;
import com.presenter.IHomePresenter;
import com.utils.LogUtils;
import com.utils.RetrofitManager;
import com.view.IHomeCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {
    private IHomeCallback mCallback = null;

    @Override
    public void getCategories() {
        if(mCallback != null){
            mCallback.onLoading();
        }
        //加载分类数据
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        API api = retrofit.create(API.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //数据结果
                int code = response.code();
                LogUtils.d(HomePresenterImpl.this,"result code is --> " + code);
                if(code == HttpURLConnection.HTTP_OK){
                    //请求成功
                    Categories categories = response.body();
                    if(mCallback != null){
                        //categories = null;
                        Log.d("gg",categories.toString());
                        if(categories == null || categories.getData().size() == 0){
                            mCallback.onEmpty();

                        }else{
                            LogUtils.d(HomePresenterImpl.this,categories.toString());
                            mCallback.onCategoriesLoaded(categories);
                        }
                    }else{
                        Log.d("dd","dd");
                    }
                }else{
                    //请求失败
                    LogUtils.i(HomePresenterImpl.this,"请求失败");
                    if(mCallback != null){
                        mCallback.onError();
                    }
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                //加载失败的结果
                LogUtils.e(HomePresenterImpl.this,"请求错误。。。"+t.toString());
                if(mCallback != null){
                    mCallback.onError();
                }
            }
        });

    }

    @Override
    public void registerViewCallback(IHomeCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IHomeCallback callback) {
        this.mCallback = null;
    }
}
