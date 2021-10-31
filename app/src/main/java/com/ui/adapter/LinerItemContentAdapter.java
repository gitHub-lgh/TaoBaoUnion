package com.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.model.domain.ILinerItemInfo;
import com.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinerItemContentAdapter extends RecyclerView.Adapter<LinerItemContentAdapter.InnerHolder> {

    List<ILinerItemInfo> data = new ArrayList<>();
    private OnListeItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        ILinerItemInfo dataBean = data.get(position);
        //设置数据
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener!=null) {
                    //HomePagerContent.DataBean item = data.get(position);
                    mItemClickListener.onItemClick(dataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<? extends ILinerItemInfo> contents) {
        data.clear();
        data.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<? extends ILinerItemInfo> contents) {
        //拿到之前数据的size
        int olderSize = data.size();
        data.addAll(contents);
        //更新UI
        notifyItemRangeChanged(olderSize,contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView titleTv;

        @BindView(R.id.goods_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.goods_after_off_prise)
        public TextView finalPriseTv;

        @BindView(R.id.goods_original_prise)
        public TextView originalPriseTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTv;

        public InnerHolder(@NonNull View itemView){
            super((itemView));
            ButterKnife.bind(this,itemView);
        }

        public void setData(ILinerItemInfo dataBean) {

            Context context = itemView.getContext();
            String titile = dataBean.getTitle();
            String pictUrl = dataBean.getCover();
            long couponAmount = dataBean.getCouponAmount();
            String finalPrise = dataBean.getFinalPrise();
            float resultPrise = Float.parseFloat(finalPrise) - couponAmount;
            long sellCount = dataBean.getVolume();

//            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
//            int width = layoutParams.width;
//            int height = layoutParams.height;
//            int coverSize = (width > height ? width : height) / 2;
            if(!TextUtils.isEmpty(pictUrl)){
                String coverPath = UrlUtils.getCoverPath(pictUrl);
                Glide.with(context).load(coverPath).into(cover);
            }else{
                cover.setImageResource(R.mipmap.ic_launcher);
            }


            titleTv.setText(titile);
            offPriseTv.setText(String.format(context.getString(R.string.text_goods_off_prise),couponAmount));
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            originalPriseTv.setText(String.format(context.getString(R.string.text_goods_original_prise),finalPrise));
            finalPriseTv.setText(String.format("%.2f",resultPrise));
            sellCountTv.setText(String.format(context.getString(R.string.text_goods_sell_count),sellCount));

        }
    }
    public void setOnListeItemClickListener(OnListeItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public interface OnListeItemClickListener{
        void onItemClick(ILinerItemInfo item);
    }
}
