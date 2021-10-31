package com.utils;

import com.presenter.IOnsellPagerPresenter;
import com.presenter.ISearchPresenter;
import com.presenter.ISelectedPagePresenter;
import com.presenter.ITicketPresenter;
import com.presenter.impl.CategoryPagePresenterImpl;
import com.presenter.impl.HomePresenterImpl;
import com.presenter.impl.OnSellPagerPresenterImpl;
import com.presenter.impl.SearchPresenteImpl;
import com.presenter.impl.SelectedPagePresenterImpl;
import com.presenter.impl.TicketPresenterImpl;

public class PresenterManager {
    private static final PresenterManager ourInstance = new PresenterManager();
    private final CategoryPagePresenterImpl mCategoryPagePresenter;
    private final HomePresenterImpl mHomePresenter;
    private final ITicketPresenter mTicketPresenter;
    private final ISelectedPagePresenter selectedPagePresenter;
    private final IOnsellPagerPresenter onsellPagerPresenter;
    private final ISearchPresenter mSearchPagePresenter;

    public static PresenterManager getInstance(){
        return ourInstance;
    }

    private PresenterManager(){
        mCategoryPagePresenter = new CategoryPagePresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        selectedPagePresenter = new SelectedPagePresenterImpl();
        onsellPagerPresenter = new OnSellPagerPresenterImpl();
        mSearchPagePresenter = new SearchPresenteImpl();
    }
    public CategoryPagePresenterImpl getmCategoryPagePresenter() {
        return mCategoryPagePresenter;
    }

    public HomePresenterImpl getmHomePresenter() {
        return mHomePresenter;
    }

    public ITicketPresenter getmTicketPresenter() {
        return mTicketPresenter;
    }

    public IOnsellPagerPresenter getOnsellPagerPresenter() {
        return onsellPagerPresenter;
    }

    public ISearchPresenter getmSearchPagePresenter() {
        return mSearchPagePresenter;
    }

    public ISelectedPagePresenter getSelectedPagePresenter() {
        return selectedPagePresenter;
    }
}
