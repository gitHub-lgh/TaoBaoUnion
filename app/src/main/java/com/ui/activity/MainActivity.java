package com.ui.activity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.base.BaseFragment;
import com.example.taobaounion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ui.fragment.HomeFragment;
import com.ui.fragment.OnSellFragment;
import com.ui.fragment.SearchFragment;
import com.ui.fragment.SelectedFragment;
import com.utils.LogUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity{
    @BindView(R.id.navigation_bar)
    BottomNavigationView mNavigationView;
    private HomeFragment homeFragment;
    private SelectedFragment selectedFragment;
    private OnSellFragment redPacketFragment;
    private SearchFragment searchFragment;
    private FragmentManager fm;

    @Override
    protected void initView() {
        initFragments();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initEvent() {
        initListener();
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        selectedFragment = new SelectedFragment();
        redPacketFragment = new OnSellFragment();
        searchFragment = new SearchFragment();
        switchFragment(homeFragment);
    }


    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                LogUtils.d(this,"首页");
                switchFragment(homeFragment);
            }
            else if(item.getItemId() == R.id.red_packet){
                LogUtils.i(this,"特惠");
                switchFragment(redPacketFragment);
            }
            else if(item.getItemId() == R.id.selected){
                LogUtils.w(this,"精选");
                switchFragment(selectedFragment);
            }
            else if(item.getItemId() == R.id.search){
                LogUtils.e(this,"搜索");
                switchFragment(searchFragment);
            }
            return true;
        });
    }

    /**
     * 上一次显示的fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment fragmentTarget) {
        if(fragmentTarget != lastOneFragment){
            //修改成add和hide的方式来控制Fragment的切换
            fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            if(!fragmentTarget.isAdded()){
                fragmentTransaction.add(R.id.main_page_container,fragmentTarget);
            }else{
                fragmentTransaction.show(fragmentTarget);
            }
            if(lastOneFragment != null){
                fragmentTransaction.hide(lastOneFragment);
            }
            lastOneFragment = fragmentTarget;
            fragmentTransaction.commit();
        }
    }

    /**
     * 跳转到搜索界面
     */
    @Override
    public void switch2Search() {
        switchFragment(searchFragment);
        //切换底部导航栏选中
        mNavigationView.setSelectedItemId(R.id.search);
    }
}
