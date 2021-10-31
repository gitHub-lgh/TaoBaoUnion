package com.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.model.domain.TicketResult;
import com.presenter.ITicketPresenter;
import com.utils.LogUtils;
import com.utils.PresenterManager;
import com.utils.ToastUtil;
import com.utils.UrlUtils;
import com.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private ITicketPresenter mTicketPresenter;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_code)
    public EditText mCode;

    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mCopyOrOpen;

    @BindView(R.id.ticket_back)
    public ImageView mTicketBack;

    @BindView(R.id.ticket_loadingView)
    public View loadingView;

    @BindView(R.id.ticket_cover_retry)
    public TextView retryLoad;

    private boolean mHasTabaoApp;

    @Override
    protected void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getmTicketPresenter();
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallback(this);
        }
        //检擦是否有安装淘宝
        //淘宝包名：com.taobao.taobao
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTabaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTabaoApp = false;
        }
        //根据这个值去更新UI
        mCopyOrOpen.setText(mHasTabaoApp ? "打开淘宝领卷" : "复制淘口令");
    }
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initEvent() {
        mTicketBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //设置复制/领卷按钮的点击事件
        mCopyOrOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //复制淘口令
                //拿到内容
                String ticketCode = mCode.getText().toString().trim();
                LogUtils.d(TicketActivity.this,"TicketCode ---> "+ticketCode);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code",ticketCode);
                cm.setPrimaryClip(clipData);
                //判断有没有淘宝
                if(mHasTabaoApp){
                    //如果有就打开
                    Intent taobaoIntent = new Intent();
                    ComponentName componentName = new ComponentName("com.taobao.taobao","com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);
                }else{
                    //没有就提示复制成功
                    ToastUtil.showToast("已经复制，粘贴分享，或打开淘宝");
                }
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {
        if ( mCover != null && !TextUtils.isEmpty(cover)) {
            String coverPath = UrlUtils.getCoverPath(cover);
            //LogUtils.d(this,""+coverPath);
            Glide.with(this).load(coverPath).into(mCover);
        }
        if(result != null && result.getData().getTbk_tpwd_create_response() != null){
            mCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoad != null) {
            retryLoad.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (retryLoad != null) {
            retryLoad.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEmpty() {

    }
}
