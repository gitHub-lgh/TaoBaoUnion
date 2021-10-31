package com.ui.fragment;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseFragment;
import com.example.taobaounion.R;
import com.model.domain.IBaseInfo;
import com.model.domain.SelectedContent;
import com.model.domain.SelectedContent3;
import com.model.domain.SelectedPageCategory;
import com.presenter.ISelectedPagePresenter;
import com.ui.adapter.SelectedPageContentAdapter;
import com.ui.adapter.SelectedPageLeftAdapter;
import com.utils.LogUtils;
import com.utils.PresenterManager;
import com.utils.SizeUtils;
import com.utils.TicketUtils;
import com.view.ISelectedPageCallback;

import java.util.List;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback, SelectedPageLeftAdapter.OnLeftItemClickListener, SelectedPageContentAdapter.OnSelectedPageContentClickLinstener {

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;

    @BindView(R.id.right_content_list)
    public RecyclerView rightContentList;

    @BindView(R.id.bar_title_tv)
    public TextView barTitleTv;

    private ISelectedPagePresenter selectedPagePresenter;
    private SelectedPageLeftAdapter mSelectedPageLeftAdapter;
    private SelectedPageContentAdapter mRightAdapter;

    @Override
    protected int getRootViewresId() {
        return R.layout.fragment_selected;
    }

    @Override
    protected void initpresenter() {
        super.initpresenter();
        selectedPagePresenter = PresenterManager.getInstance().getSelectedPagePresenter();
        selectedPagePresenter.registerViewCallback(this);
        selectedPagePresenter.getCategories();

    }

    @Override
    protected void release() {
        super.release();
        if (selectedPagePresenter != null) {
            selectedPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initView(View rootView) {
        barTitleTv.setText("精选宝贝");
        setUpState(State.LOADING);
        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSelectedPageLeftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(mSelectedPageLeftAdapter);

        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRightAdapter = new SelectedPageContentAdapter();
        rightContentList.setAdapter(mRightAdapter);
        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(),4);
                int leftAndRight = SizeUtils.dip2px(getContext(),6);
                outRect.left = leftAndRight;
                outRect.right = leftAndRight;
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
            }
        });
        setUpState(State.SUCCESS);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSelectedPageLeftAdapter.setOnLeftItemClickListener(this);
        mRightAdapter.setOnSelectedPageContentClickLinstener(this);
    }

    @Override
    public void onCategoriesLoaded(SelectedPageCategory categories) {
        if(categories == null){
            return;
        }
        setUpState(State.SUCCESS);
        mSelectedPageLeftAdapter.setData(categories);
        //分类内容
        LogUtils.d(this,"categories ---> " + categories);
        //根据当前选中的分类，获取分类详情内容
        List<SelectedPageCategory.DataBean> data = categories.getData();
        selectedPagePresenter.getContentByCategory(data.get(0));
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    @Override
    public void onContentLoad(SelectedContent content) {
        mRightAdapter.setData(content);
        rightContentList.scrollToPosition(0);
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

    }

    @Override
    public void onLeftItemClick(SelectedPageCategory.DataBean item) {
        //左边的分类点击了
        selectedPagePresenter.getContentByCategory(item);
        LogUtils.d("this","title ---> " + item.getFavorites_title());
    }

    @Override
    protected void onRetryClick() {
        if(selectedPagePresenter != null){
            selectedPagePresenter.reloadContent();
        }

    }

    @Override
    public void onContentItemListener(IBaseInfo item) {
//        //内容被点击了
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
