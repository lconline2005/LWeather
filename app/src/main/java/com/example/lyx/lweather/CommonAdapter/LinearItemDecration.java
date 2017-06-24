package com.example.lyx.lweather.CommonAdapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/6/19.
 * linear样式
 */

public class LinearItemDecration extends RecyclerView.ItemDecoration{
    //网上很多都是用的系统的一个属性 android.R.attrs.listDriver
    private Drawable mDrawable;
    public LinearItemDecration(Context context, int DrawableResourceId){
        //获取drawable
        mDrawable= ContextCompat.getDrawable(context,DrawableResourceId);
    }



    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //判断是否在第一条，在顶部添加好于在底部
        int position = parent.getChildAdapterPosition(view);
        if (position != 0) {
            //在每个顶部留10个像素来绘制分割线
            outRect.top = mDrawable.getIntrinsicHeight();
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //在每一个头部绘制
        int childCount = parent.getChildCount();
        //制定绘制区域
        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft();
        rect.right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 1; i < childCount; i++) {
            //分隔线的额底部是itemview的头部
            rect.bottom = parent.getChildAt(i).getTop();
            rect.top = rect.bottom - mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(rect);
            mDrawable.draw(c);
        }
    }

}
