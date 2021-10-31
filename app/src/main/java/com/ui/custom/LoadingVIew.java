package com.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.taobaounion.R;
import com.utils.LogUtils;

public class LoadingVIew extends AppCompatImageView {
    private float mDegrees = 0;
    private boolean mNeedRotate = false;

    public LoadingVIew(Context context) {
        this(context,null);
    }

    public LoadingVIew(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        startRotate();
    }

    private void startRotate() {
        post(new Runnable() {
            @Override
            public void run() {
                mDegrees += 10;
                if (mDegrees >= 360) {
                    mDegrees = 0;
                }
                invalidate();
                //判断是否要继续旋转
                //如果不可见，或者已经DetachedFromWindow就不再转动了
                if(getVisibility() != VISIBLE && !mNeedRotate){
                    LogUtils.d(LoadingVIew.this,"onDetachedFromWindow()2");
                    removeCallbacks(this);
                }else{
                    postDelayed(this,15);
                    //LogUtils.d(LoadingVIew.this,"loading......");
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    private void stopRotate() {
        mNeedRotate = false;
        LogUtils.d(LoadingVIew.this,"onDetachedFromWindow()");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegrees,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
