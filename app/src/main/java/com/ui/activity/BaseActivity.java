package com.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        mBind = ButterKnife.bind(this);
        initView();
        initEvent();
        initPresenter();
    }

    protected abstract void initPresenter();

    //protected abstract void release();
    protected void initEvent() {

    }

    protected  void initView(){};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBind != null){
            mBind.unbind();
        }
    }

    protected abstract int getLayoutResId();

}
