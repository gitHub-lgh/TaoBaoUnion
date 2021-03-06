package com.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.base.BaseFragment;
import com.example.taobaounion.R;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.model.domain.Categories;
import com.model.domain.HomePagerContent;
import com.model.domain.IBaseInfo;
import com.model.domain.ILinerItemInfo;
import com.presenter.ICategoryPagerPresenter;
import com.ui.adapter.LinerItemContentAdapter;
import com.ui.adapter.LooperPagerAdapter;
import com.ui.custom.AutoLoopViewPager;
import com.ui.custom.TbNestedScrollView;
import com.utils.Constants;
import com.utils.LogUtils;
import com.utils.PresenterManager;
import com.utils.SizeUtils;
import com.utils.TicketUtils;
import com.utils.ToastUtil;
import com.view.ICategoryPagerCallback;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, LinerItemContentAdapter.OnListeItemClickListener, LooperPagerAdapter.OnLooperPagerItemClickListener {

    private ICategoryPagerPresenter mCategoryPagerPresenter;
    private int materialId;
    private LinerItemContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category){
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KET_HOME_PAGER_TITLE,category.getTitle());
        bundle.putInt(Constants.KET_HOME_PAGER_MATERIAL_ID,category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.looper_pager)
    public AutoLoopViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout twinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homeHeaderContainer;

    @BindView(R.id.home_pager_nested_scroller)
    public TbNestedScrollView homePagerNestedView;

    @Override
    protected int getRootViewresId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void onResume() {
        //????????????????????????????????????
        super.onResume();
        looperPager.startLoop();
    }

    @Override
    public void onPause() {
        //????????????????????????
        super.onPause();
        looperPager.stopLoop();
    }

    @Override
    protected void initView(View rootView) {
        //?????????????????????
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });
        //???????????????
        mContentAdapter = new LinerItemContentAdapter();
        //???????????????
        mContentList.setAdapter(mContentAdapter);
        //????????????????????????
        mLooperPagerAdapter = new LooperPagerAdapter();
        //???????????????
        looperPager.setAdapter(mLooperPagerAdapter);
        //??????Refresh????????????
        twinklingRefreshLayout.setEnableLoadmore(true);
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableOverScroll(false);
    }

    @Override
    protected void initpresenter() {
        mCategoryPagerPresenter = PresenterManager.getInstance().getmCategoryPagePresenter();
        mCategoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnListeItemClickListener(this);
        mLooperPagerAdapter.setOnlooperPagerItemClickListener(this);
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(homeHeaderContainer == null){
                    //LogUtils.d(HomePagerFragment.this,"homeHeaderContainer ---> null " );
                    return;
                }
                int headerHeight = homeHeaderContainer.getMeasuredHeight();
                homePagerNestedView.setHeaderHeight(headerHeight);
                int measuredHeight = homePagerParent.getMeasuredHeight();
                //LogUtils.d(HomePagerFragment.this,"measureHeight -- > " + measuredHeight);
                ViewGroup.LayoutParams layoutParams = mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if(measuredHeight != 0){
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mLooperPagerAdapter.getDataSize() == 0){
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                //???????????????
                updateLooperIndicator(targetPosition);
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        twinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                mCategoryPagerPresenter.loaderMore(materialId);
            }
        });
    }

    /**
     * ???????????????
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for(int i = 0;i < looperPointContainer.getChildCount();i++){
            View point = looperPointContainer.getChildAt(i);
            if(i == targetPosition){
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            }else{
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }

    }

    @Override
    protected void loadData() {
        Bundle argument = getArguments();
        String title = argument.getString(Constants.KET_HOME_PAGER_TITLE);
        materialId = argument.getInt(Constants.KET_HOME_PAGER_MATERIAL_ID);
        //????????????
        //LogUtils.d(this,"title --> " + title);
        //LogUtils.d(this,"materialId --> " + materialId);
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.getContentByCategoryId(materialId);
        }
        if(currentCategoryTitleTv != null){
            currentCategoryTitleTv.setText(title);
        }
    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataBean> contents) {
        //??????????????????
        mContentAdapter.setData(contents);
        setUpState(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return materialId;
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onError() {
        //????????????
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onLoadMoreError() {
        ToastUtil.showToast("??????????????????????????????");
//        if (twinklingRefreshLayout != null) {
//            twinklingRefreshLayout.finishLoadmore();
//        }
    }

    @Override
    public void onLoadMoreEmpty() {
        ToastUtil.showToast("?????????????????????");
//        if (twinklingRefreshLayout != null) {
//            twinklingRefreshLayout.finishLoadmore();
//        }
    }

    @Override
    public void onLoadMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //?????????????????????????????????
         mContentAdapter.addData(contents);
//        if (twinklingRefreshLayout != null) {
//            twinklingRefreshLayout.finishLoadmore();
//        }
        ToastUtil.showToast("?????????"+contents.size()+"?????????");
    }

    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
        mLooperPagerAdapter.setData(contents);
        //?????????%??????size????????????0????????????????????????????????????
        //?????????
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;
        LogUtils.d(this,"url --> " + contents.get(0).getPict_url());
        //??????????????????,??????????????????????????????????????????
        looperPager.setCurrentItem(targetCenterPosition);
        looperPointContainer.removeAllViews();
        //?????????
        for(int i = 0;i < contents.size();i++){
            View point = new View(getContext());
            int size = SizeUtils.dip2px(getContext(),8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size,size);
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(),5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(),5);
            point.setLayoutParams(layoutParams);
            if(i == 0){
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            }else{
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }

            looperPointContainer.addView(point);
        }
    }

    @Override
    protected void release(){
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(ILinerItemInfo item) {
        //?????????????????????
        LogUtils.d(this,"item click ---> "+item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
//        //????????????
//        String title = item.getTitle();
//        //????????????/???????????????
//        String url = item.getCoupon_click_url();
//        if(TextUtils.isEmpty(url)){
//            url = item.getClick_url();
//        }
//        String cover = item.getPict_url();
//        //??????ticketPresenter???????????????
//        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getmTicketPresenter();
//        ticketPresenter.getTicket(title,url,cover);
//        startActivity(new Intent(getContext(), TicketActivity.class));
        TicketUtils.toTicketPage(getContext(),item);
    }

    @Override
    public void onLooperItemClick(HomePagerContent.DataBean item) {
        //??????????????????
        LogUtils.d(this,"looper click --->"+item.getTitle());
        handleItemClick(item);
    }
}
