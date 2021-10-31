package com.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.base.BaseFragment;
import com.example.taobaounion.R;
import com.google.android.material.tabs.TabLayout;
import com.model.domain.Categories;
import com.presenter.IHomePresenter;
import com.presenter.impl.HomePresenterImpl;
import com.ui.activity.MainActivity;
import com.ui.activity.ScanerCodeActivity;
import com.ui.adapter.HomePagerAdapter;
import com.utils.LogUtils;
import com.utils.PresenterManager;
import com.view.IHomeCallback;
import com.vondear.rxfeature.activity.ActivityScanerCode;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallback {
    @BindView(R.id.home_indicator)
    public TabLayout mTabLayout;

    @BindView(R.id.home_input_box)
    public View mInputBox;

    @BindView(R.id.scan_icon)
    public View Scanicon;

    private IHomePresenter mHomePresenter;

    @BindView(R.id.home_pager)
    public ViewPager homePager;
    private HomePagerAdapter homePagerAdapter;

    @Override
    protected int getRootViewresId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {
        mTabLayout.setupWithViewPager(homePager);
        //给ViewPager设置适配器
        homePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        homePager.setAdapter(homePagerAdapter);
    }

    @Override
    protected void initpresenter() {
        //创建Presenter
        mHomePresenter = PresenterManager.getInstance().getmHomePresenter();
        mHomePresenter.registerViewCallback(this);
    }

    @Override
    protected void initListener() {
        mInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到搜索页面
                FragmentActivity activity = getActivity();
                if(activity instanceof MainActivity){
                    ((MainActivity) activity).switch2Search();
                }
            }
        });
        Scanicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到扫码界面
                startActivity(new Intent(getContext(), ScanerCodeActivity.class));
            }
        });
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout,container,false);
    }

    @Override
    protected void loadData(){
        //加载数据
        mHomePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {
            setUpState(State.SUCCESS);
        //加载的数据就会从这里回来
        Log.d("ss","ss");
        LogUtils.d(this,"onCategoriesLoaded");
        if(homePagerAdapter != null){
            homePagerAdapter.setCategories(categories);
        }
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }


    @Override
    protected void release() {
        //取消回调注册
        if(mHomePresenter != null){
            mHomePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void onRetryClick() {
        //网络错误，点击重试
        //重新加载分类
        if(mHomePresenter != null){
            mHomePresenter.getCategories();
        }
    }
}
