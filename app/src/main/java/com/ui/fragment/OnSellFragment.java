package com.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseFragment;
import com.example.taobaounion.R;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.model.domain.IBaseInfo;
import com.model.domain.OnSellContent;
import com.presenter.IOnsellPagerPresenter;
import com.presenter.ITicketPresenter;
import com.ui.activity.TicketActivity;
import com.ui.adapter.OnSellContentAdapter;
import com.utils.PresenterManager;
import com.utils.SizeUtils;
import com.utils.TicketUtils;
import com.utils.ToastUtil;
import com.view.IOnSellPagerCallback;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPagerCallback, OnSellContentAdapter.OnSellPageItemClickListener {

    private IOnsellPagerPresenter mOnSellPagerPresenter;
    private OnSellContentAdapter mOnSellContentAdapter;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView onSellContentList;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mTwinklingRefresh;

    @BindView(R.id.bar_title_tv)
    public TextView barTitleTv;


    @Override
    protected int getRootViewresId() {
        return R.layout.fragment_on_sell;
    }
    @Override
    protected void initView(View rootView) {
        barTitleTv.setText("特惠宝贝");
        //设置适配器
        mOnSellContentAdapter = new OnSellContentAdapter();
        onSellContentList.setAdapter(mOnSellContentAdapter);
        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        onSellContentList.setLayoutManager(gridLayoutManager);
        onSellContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = SizeUtils.dip2px(getContext(),2.5f);
                outRect.top = SizeUtils.dip2px(getContext(),2.5f);
                outRect.left = SizeUtils.dip2px(getContext(),2.5f);
                outRect.right = SizeUtils.dip2px(getContext(),2.5f);
            }
        });
        mTwinklingRefresh.setEnableLoadmore(true);
        mTwinklingRefresh.setEnableRefresh(false);
        mTwinklingRefresh.setEnableOverScroll(true);
    }

    @Override
    protected void initpresenter() {
        super.initpresenter();
        mOnSellPagerPresenter = PresenterManager.getInstance().getOnsellPagerPresenter();
        mOnSellPagerPresenter.registerViewCallback(this);
        mOnSellPagerPresenter.getOnSellContent();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTwinklingRefresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //去加载更多内容
                if (mOnSellPagerPresenter != null) {
                    mOnSellPagerPresenter.loaderMore();
                }
            }
        });
        mOnSellContentAdapter.setOnSellPageItemClickListener(this);

    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    @Override
    protected void onRetryClick() {
        if (mOnSellPagerPresenter != null) {
            mOnSellPagerPresenter.reLoad();
        }
    }

    @Override
    protected void release() {
        super.release();
        if (mOnSellPagerPresenter != null) {
            mOnSellPagerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        //数据回来
        setUpState(State.SUCCESS);
        //更新UI
        mOnSellContentAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        //数据从这里回来
        mTwinklingRefresh.finishLoadmore();
        //添加内容到适配器里
        mOnSellContentAdapter.onMoreLoaded(moreResult);
        int size = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        ToastUtil.showToast("加载了" + size + "个宝贝");
    }

    @Override
    public void onMoreLoadedError() {
        mTwinklingRefresh.finishLoadmore();
        ToastUtil.showToast("网络错误，请稍后重试。。。");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mTwinklingRefresh.finishLoadmore();
        ToastUtil.showToast("没有更多宝贝了。。。");
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
    public void onSellItemClick(IBaseInfo item) {
//        //特惠列表内容被点击
//        //处理数据
//        String title = item.getTitle();
//        //详情地址/优惠卷地址
//        String url = item.getCoupon_click_url();
//        if(TextUtils.isEmpty(url)){
//            url = item.getClick_url();
//        }
//        String cover = item.getPict_url();
//        //拿到ticketPresenter去加载数据
//        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getmTicketPresenter();
//        ticketPresenter.getTicket(title,url,cover);
//        startActivity(new Intent(getContext(), TicketActivity.class));
        TicketUtils.toTicketPage(getContext(),item);
    }
}
