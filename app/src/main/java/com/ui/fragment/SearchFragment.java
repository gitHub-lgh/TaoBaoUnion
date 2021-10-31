package com.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseFragment;
import com.example.taobaounion.R;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.model.domain.Histories;
import com.model.domain.ILinerItemInfo;
import com.model.domain.SearchRecommend;
import com.model.domain.SearchResult;
import com.presenter.ISearchPresenter;
import com.ui.adapter.LinerItemContentAdapter;
import com.ui.custom.TextFlowLayout;
import com.utils.KeyboardUtil;
import com.utils.LogUtils;
import com.utils.PresenterManager;
import com.utils.SizeUtils;
import com.utils.TicketUtils;
import com.utils.ToastUtil;
import com.view.ISearchPageCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchPageCallback, TextFlowLayout.OnFlowTextItemClickListener {

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoriesView;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_history_container)
    public LinearLayout mHistoriesContainer;

    @BindView(R.id.search_recommend_container)
    public LinearLayout mRecommendContainer;

    @BindView(R.id.search_history_deleted)
    public ImageView mHistoryDeleted;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchResultList;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mSearchClean;

    @BindView(R.id.search_input_box)
    public EditText mSearchInput;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    private ISearchPresenter mSearchPresenter;
    private LinerItemContentAdapter mSearchPageAdapter;

    @Override
    protected void initpresenter() {
        super.initpresenter();
        mSearchPresenter = PresenterManager.getInstance().getmSearchPagePresenter();
        mSearchPresenter.registerViewCallback(this);
        //获取搜索推荐词
        mSearchPresenter.getRecommendWords();
        mSearchPresenter.getHistories();
    }

    @Override
    protected void onRetryClick() {
        if (mSearchPresenter != null) {
            mSearchPresenter.research();
        }
    }

    @Override
    protected void release() {
        if(mSearchPresenter != null){
            mSearchPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initListener() {
        mHistoriesView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
        //删除历史记录
        mHistoryDeleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchPresenter.delHistories();
            }
        });

        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多内容
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });
        //跳转到淘口令界面
        mSearchPageAdapter.setOnListeItemClickListener(new LinerItemContentAdapter.OnListeItemClickListener() {
            @Override
            public void onItemClick(ILinerItemInfo item) {
                TicketUtils.toTicketPage(getContext(),item);
            }
        });
        //监听搜索按钮
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断输入框是否有内容，有则搜索
                if(hasInput(false)){
                    //发起搜索
                    if(mSearchPresenter != null){
                        toSearch(mSearchInput.getText().toString().trim());
                        //mSearchPresenter.dosearch(mSearchInput.getText().toString().trim());
                        KeyboardUtil.hide(getContext(),view);
                    }
                }else{
                    //隐藏键盘
                    KeyboardUtil.hide(getContext(),view);
                }
            }
        });

        //清除输入框里的内容
        mSearchClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchInput.setText("");
                //回到历史记录里面
                switch2HistoryPage();
            }
        });

        //监听输入框的内容变化
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //变化时候的通知
                //如果长度不为0，那么显示删除按钮，否则显示隐藏
                mSearchClean.setVisibility(hasInput(true)?View.VISIBLE:View.GONE);
                mSearchBtn.setText(hasInput(false)?"搜索":"取消");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //键盘发起搜索
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null){
                    String keyword = textView.getText().toString().trim();
                    //判断拿到的内容是否为空
                    if(TextUtils.isEmpty(keyword)){
                        return false;
                    }
                    //发起搜索
                    toSearch(keyword);
                    //mSearchPresenter.dosearch(keyword);
                }
                return false;
            }
        });
    }

    /**
     * 切换到历史和推荐词界面
     */
    private void switch2HistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
        if(mRecommendView.getContentSize() != 0){
            mRecommendContainer.setVisibility(View.VISIBLE);
        }else{
            mRecommendContainer.setVisibility(View.GONE);
        }
        //隐藏搜索内容
        mRefreshContainer.setVisibility(View.GONE);
    }

    /**
     * 判断搜索输入框是否为空
     */
    private boolean hasInput(boolean containSpace){
        //是否删除首尾空格
        if(containSpace){
            return mSearchInput.getText().toString().length() > 0;
        }else{
            return mSearchInput.getText().toString().trim().length() > 0;
        }
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_with_bar_layout,container,false);
    }

    @Override
    protected int getRootViewresId() {
        return R.layout.fragment_search;
    }
    @Override
    protected void initView(View rootView) {
        setUpState(State.LOADING);
        //设置布局管理器
        mSearchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchPageAdapter = new LinerItemContentAdapter();
        mSearchResultList.setAdapter(mSearchPageAdapter);
        //设置刷新控件
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
        //设置间距
        mSearchResultList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
        setUpState(State.SUCCESS);
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        if(histories == null || histories.getHistories().size() == 0){
            mHistoriesContainer.setVisibility(View.GONE);
        }else{
            mHistoriesContainer.setVisibility(View.VISIBLE);
            mHistoriesView.setTextList(histories.getHistories());
        }

    }

    @Override
    public void onHistoriesDeleted() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
        //LogUtils.d(this,"dosearch result ---> " + result);
        //隐藏掉历史记录和推荐
        mRecommendContainer.setVisibility(View.GONE);
        mHistoriesContainer.setVisibility(View.GONE);
        //显示搜索界面
        mRefreshContainer.setVisibility(View.VISIBLE);
        //设置数据
        mSearchPageAdapter.setData(result.getData()
                .getTbk_dg_material_optional_response()
                .getResult_list()
                .getMap_data());
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        mRefreshContainer.finishLoadmore();
        //加载到更多内容
        //去拿到结果,添加到适配器尾部
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchPageAdapter.addData(moreData);
        //提示用户加载到更多内容
        ToastUtil.showToast("加载了"+moreData.size()+"个宝贝");
    }

    @Override
    public void onMoreLoadedError() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("网路错误，请稍后重试");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("没有更多内容了");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {
        LogUtils.d(this,"recommendWords size--> " + recommendWords.size());
        List<String> recommendKeyWords = new ArrayList<>();
        for (SearchRecommend.DataBean recommendWord : recommendWords) {
            recommendKeyWords.add(recommendWord.getKeyword());
        }
        if(recommendWords == null || recommendWords.size() == 0){
            mRecommendContainer.setVisibility(View.GONE);
        }else{
            mRecommendView.setTextList(recommendKeyWords);
            mRecommendContainer.setVisibility(View.VISIBLE);
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
    public void onFlowItemClick(String text) {
        //发起搜索
        toSearch(text);
    }

    private void toSearch(String text) {
        if (mSearchPresenter != null) {
            mSearchResultList.scrollToPosition(0);
            mSearchInput.setText(text);
            mSearchInput.setFocusable(true);
            mSearchInput.requestFocus();
            mSearchInput.setSelection(text.length(),text.length());
            mSearchPresenter.dosearch(text);
        }
    }
}
