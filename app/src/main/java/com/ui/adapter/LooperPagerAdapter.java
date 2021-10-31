package com.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.model.domain.HomePagerContent;
import com.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class LooperPagerAdapter extends PagerAdapter {
    List<HomePagerContent.DataBean> mData = new ArrayList<>();
    private OnLooperPagerItemClickListener mItemClickListener = null;

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int getDataSize(){
        return mData.size();
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //处理越界问题
        //size = 5
        //0 --> 0
        //1 --> 1
        //2 --> 2
        //3 --> 3
        //4 --> 4
        //5 --> 0
        //6 --> 1
        int realPosition = position % mData.size();
        HomePagerContent.DataBean dataBean = mData.get(realPosition);
        int measuredHeight = container.getMeasuredHeight();
        int measuredWidth = container.getMeasuredWidth();
        int ivSize = (measuredHeight > measuredWidth ? measuredHeight : measuredWidth) / 2;
        String coverUrl = UrlUtils.getCoverPath(dataBean.getPict_url(),ivSize);
        ImageView iv = new ImageView(container.getContext());
        //设置轮播图点击事件
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mItemClickListener != null){
                    HomePagerContent.DataBean item = mData.get(realPosition);
                    mItemClickListener.onLooperItemClick(item);
                }
            }
        });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(layoutParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(container.getContext()).load(coverUrl).into(iv);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }
    public void setOnlooperPagerItemClickListener(OnLooperPagerItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public interface OnLooperPagerItemClickListener{
        void onLooperItemClick(HomePagerContent.DataBean item);
    }
}
