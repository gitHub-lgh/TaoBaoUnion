package com.presenter.impl;

import com.model.API;
import com.model.domain.Histories;
import com.model.domain.SearchRecommend;
import com.model.domain.SearchResult;
import com.presenter.ISearchPresenter;
import com.utils.JsonCacheUtil;
import com.utils.LogUtils;
import com.utils.RetrofitManager;
import com.view.ISearchPageCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenteImpl implements ISearchPresenter {

    private final API mApi;
    private ISearchPageCallback mSearchPageCallback = null;
    private final JsonCacheUtil mJsonCacheUtil;
    private String mCurrentKeyWord = null;

    public SearchPresenteImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(API.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    private static int DEFAULT_PAGE = 0;
    /**
     * 搜索的当前页
     */
    private int mcurrentPage = DEFAULT_PAGE;


    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES,Histories.class);
        if(mSearchPageCallback != null) {
            mSearchPageCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORIES);
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onHistoriesDeleted();
        }
    }

    public static final String KEY_HISTORIES = "key_histories";

    public int DEFAULT_HISTORIES_SIZE = 10;
    private int mHistoriesMaxSize = DEFAULT_HISTORIES_SIZE;

    /**
     * 添加历史记录
     * @param history
     */
    private void saveHistory(String history){

        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES,Histories.class);
        //如果说已经存在了，就干掉，然后在添加
        List<String> historiesList = null;
        if(histories != null && histories.getHistories() != null){
            historiesList = histories.getHistories();
            if(historiesList.contains(history)){
                historiesList.remove(history);
            }
        }
        //去重完成
        //处理没有数据的情况
        if(historiesList == null){
            historiesList = new ArrayList<>();
        }
        if(histories == null){
            histories = new Histories();
        }
        histories.setHistories(historiesList);
        //对个数进行限制
        if(historiesList.size() > mHistoriesMaxSize){
            historiesList = historiesList.subList(0,mHistoriesMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录
        mJsonCacheUtil.saveCache(KEY_HISTORIES,histories);
    }

    /**
     * 搜索
     * @param keyword
     */
    @Override
    public void dosearch(String keyword) {
        if( mCurrentKeyWord == null || !mCurrentKeyWord.equals(keyword)){
            this.saveHistory(keyword);
            this.mCurrentKeyWord = keyword;
        }
        //更新UI状态
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onLoading();
        }
        Call<SearchResult> task = mApi.doSearch(mcurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenteImpl.this,"dosearch code ---> " + code);
                if(code == HttpURLConnection.HTTP_OK){
                    handlerSearchResult(response.body());
                }else{
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onError();
        }
    }

    /**
     * 处理加载结果
     * @param result
     */
    private void handlerSearchResult(SearchResult result) {
        if(mSearchPageCallback != null){
            if(isResultEmpty(result)){
                //数据为空
                mSearchPageCallback.onEmpty();
            }else{
                mSearchPageCallback.onSearchSuccess(result);
            }
        }

    }
    public boolean isResultEmpty(SearchResult result){
        try{
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 重新搜索
     */
    @Override
    public void research() {
        if(mSearchPageCallback != null){
            if(mCurrentKeyWord == null){
                mSearchPageCallback.onEmpty();
            }else{
                //可以重新搜索
                this.dosearch(mCurrentKeyWord);
            }
        }
    }

    @Override
    public void loaderMore() {
        mcurrentPage++;
        //进行搜索
        if(mCurrentKeyWord == null){
            if (mSearchPageCallback != null) {
                mSearchPageCallback.onEmpty();
            }
        }else{
            //做搜索的事情
            doSearchMore();
        }
    }

    /**
     * 加载更多
     */
    private void doSearchMore() {
        Call<SearchResult> task = mApi.doSearch(mcurrentPage, mCurrentKeyWord);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenteImpl.this,"doSearchMore code ---> " + code);
                if(code == HttpURLConnection.HTTP_OK){
                    handlerMoreSearchResult(response.body());
                }else{
                    onLoadMoreError();
                }
            }
            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onLoadMoreError();
            }
        });
    }

    /**
     * 处理加载更多的结果
     * @param result
     */
    private void handlerMoreSearchResult(SearchResult result) {
        if(mSearchPageCallback != null){
            if(isResultEmpty(result)){
                //数据为空
                mSearchPageCallback.onMoreLoadedEmpty();
            }else{
                mSearchPageCallback.onMoreLoaded(result);
            }
        }
    }

    /**
     * 加载更多失败
     */
    private void onLoadMoreError() {
        mcurrentPage--;
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onMoreLoadedError();
        }
    }

    /**
     * 获取推荐词
     */
    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                LogUtils.d(this,"recommend code ---> " + code );
                if(code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    if (mSearchPageCallback != null) {
                        mSearchPageCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchPageCallback callback) {
        this.mSearchPageCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchPageCallback callback) {
        this.mSearchPageCallback = null;
    }
}
