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
        //?????????????????????
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
        //??????????????????
        mHistoryDeleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchPresenter.delHistories();
            }
        });

        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //?????????????????????
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });
        //????????????????????????
        mSearchPageAdapter.setOnListeItemClickListener(new LinerItemContentAdapter.OnListeItemClickListener() {
            @Override
            public void onItemClick(ILinerItemInfo item) {
                TicketUtils.toTicketPage(getContext(),item);
            }
        });
        //??????????????????
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????????????????????????????????????????????
                if(hasInput(false)){
                    //????????????
                    if(mSearchPresenter != null){
                        toSearch(mSearchInput.getText().toString().trim());
                        //mSearchPresenter.dosearch(mSearchInput.getText().toString().trim());
                        KeyboardUtil.hide(getContext(),view);
                    }
                }else{
                    //????????????
                    KeyboardUtil.hide(getContext(),view);
                }
            }
        });

        //???????????????????????????
        mSearchClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchInput.setText("");
                //????????????????????????
                switch2HistoryPage();
            }
        });

        //??????????????????????????????
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //?????????????????????
                //??????????????????0????????????????????????????????????????????????
                mSearchClean.setVisibility(hasInput(true)?View.VISIBLE:View.GONE);
                mSearchBtn.setText(hasInput(false)?"??????":"??????");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //??????????????????
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null){
                    String keyword = textView.getText().toString().trim();
                    //?????????????????????????????????
                    if(TextUtils.isEmpty(keyword)){
                        return false;
                    }
                    //????????????
                    toSearch(keyword);
                    //mSearchPresenter.dosearch(keyword);
                }
                return false;
            }
        });
    }

    /**
     * ?????????????????????????????????
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
        //??????????????????
        mRefreshContainer.setVisibility(View.GONE);
    }

    /**
     * ?????????????????????????????????
     */
    private boolean hasInput(boolean containSpace){
        //????????????????????????
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
        //?????????????????????
        mSearchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchPageAdapter = new LinerItemContentAdapter();
        mSearchResultList.setAdapter(mSearchPageAdapter);
        //??????????????????
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
        //????????????
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
        //??????????????????????????????
        mRecommendContainer.setVisibility(View.GONE);
        mHistoriesContainer.setVisibility(View.GONE);
        //??????????????????
        mRefreshContainer.setVisibility(View.VISIBLE);
        //????????????
        mSearchPageAdapter.setData(result.getData()
                .getTbk_dg_material_optional_response()
                .getResult_list()
                .getMap_data());
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        mRefreshContainer.finishLoadmore();
        //?????????????????????
        //???????????????,????????????????????????
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchPageAdapter.addData(moreData);
        //?????????????????????????????????
        ToastUtil.showToast("?????????"+moreData.size()+"?????????");
    }

    @Override
    public void onMoreLoadedError() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("??????????????????????????????");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("?????????????????????");
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
        //????????????
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
