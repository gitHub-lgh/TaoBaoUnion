package com.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taobaounion.R;
import com.utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

    private State currentState = State.NONE;
    private View successView;
    private View loadingView;
    private View errorView;
    private View emptyView;

    public enum State{
        NONE,LOADING,SUCCESS,ERROR,EMPTY
    }
    private Unbinder mBind;
    private FrameLayout mBaseContainer;

    @OnClick(R.id.network_error_tips)
    public void retry(){
        //点击了重新加载内容
        LogUtils.d(this,"on retry...");
        onRetryClick();
    }

    /**
     * 如果子fragment需要知道网络错误之后的点击，覆盖这个方法即可
     */
    protected void onRetryClick() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = loadRootView(inflater,container);
        mBaseContainer = rootView.findViewById(R.id.base_container);
        loadStatesView(inflater,container);
        mBind = ButterKnife.bind(this,rootView);
        initView(rootView);
        initpresenter();
        initListener();
        loadData();
        return rootView;
    }

    protected void initListener() {

    }

    protected View loadRootView(LayoutInflater inflater,ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_layout,container,false);
    }

    /**
     * 加载各种状态的View
     * @param inflater
     * @param container
     */
    private void loadStatesView(LayoutInflater inflater, ViewGroup container) {

        //成功的View
        successView = loadSuccessView(inflater,container);
        mBaseContainer.addView(successView);

        //加载中的View
        loadingView = loadLoadingView(inflater,container);
        mBaseContainer.addView(loadingView);

        //错误页面
        errorView = loadErrorView(inflater,container);
        mBaseContainer.addView(errorView);

        //内容为空页面
        emptyView = loadEmptyView(inflater,container);
        mBaseContainer.addView(emptyView);

        setUpState(State.NONE);
    }

    /**
     * 子类通过这个方法来切换状态页面
     * @param state
     */
    public void setUpState(State state){
        this.currentState = state;
        successView.setVisibility(currentState == State.SUCCESS ? View.VISIBLE : View.GONE);
        errorView.setVisibility(currentState == State.ERROR ? View.VISIBLE : View.GONE);
        loadingView.setVisibility(currentState == State.LOADING ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(currentState == State.EMPTY ? View.VISIBLE : View.GONE);

    }

    /**
     * 加载loading界面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading,container,false);
    }

    /**
     * 加载错误界面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error,container,false);
    }

    /**
     * 加载内容为空页面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(R.layout.fragment_empty,container,false);
    }

    protected void initView(View rootView){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mBind != null){
            mBind.unbind();
        }
        release();
    }

    protected void release() {
        //释放资源
    }

    protected void initpresenter() {
        //加载Presenter
    }

    protected  void loadData(){

    }

    protected View loadSuccessView(LayoutInflater inflater,ViewGroup container){
        int resId = getRootViewresId();
        return inflater.inflate(resId,container,false);
    }

    protected abstract int getRootViewresId();
}
