package com.example.lyx.lweather.CommonAdapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/6/19.
 * <p>
 * Gridview样式分割线
 */

public class GridItemDecration extends RecyclerView.ItemDecoration {
    //网上很多都是用的系统的一个属性 android.R.attrs.listDriver
    private Drawable mDrawable;

    public GridItemDecration(Context context, int DrawableResourceId) {
        //获取drawable
        mDrawable = ContextCompat.getDrawable(context, DrawableResourceId);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        outRect.bottom = mDrawable.getIntrinsicHeight();
//        outRect.right = mDrawable.getIntrinsicWidth();
        //留分割线的位置 下边和右边如果市最下面，底部和右边都不留
        int bottom = mDrawable.getIntrinsicHeight();
        int right = mDrawable.getIntrinsicWidth();
        if (isLastClonum(view, parent)) {
            right = 0;
        }

        if (isLastRow(view, parent)) {
            bottom = 0;
        }
        outRect.bottom = bottom;
        outRect.right = right;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //在每一个头部绘制
        drawHorizontal(c, parent);
        drawVirtical(c, parent);


    }

    /*是不是最后一列*/
    private boolean isLastClonum(View view, RecyclerView parent) {
        //获取当前位置
        int currentPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        return (currentPosition + 1) % spanCount == 0;
    }

    /*获取recyclerview的列数*/
    private int getSpanCount(RecyclerView parent) {
        //获取列数
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            return spanCount;
        }
        return 1;
    }

    /*是不是最后一行*/
    private boolean isLastRow(View view, RecyclerView parent) {
        //当前位置》（行数-1）列数
        //列数
        int spanCount = getSpanCount(parent);
        //行数  总条目/列数
        int rowNum = parent.getAdapter().getItemCount() % spanCount == 0 ?
                parent.getAdapter().getItemCount() / spanCount : (parent.getAdapter().getItemCount() / spanCount) + 1;
        //当前位置
        int currentPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        return (currentPosition+1) > (rowNum - 1) * spanCount;
    }


    /*
        *
        * 绘制垂直方向
        * */
    private void drawVirtical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int top = childView.getTop() - params.topMargin;
            int bottom = childView.getBottom() + params.bottomMargin;
            int left = childView.getRight() + params.rightMargin;
            int right = left + mDrawable.getIntrinsicWidth() + params.rightMargin;


            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    /*
    *
    * 绘制水平方向
    * */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int left = childView.getLeft() - params.leftMargin;
            int right = childView.getRight() + mDrawable.getIntrinsicWidth() + params.rightMargin;
            int top = childView.getBottom();
            int bottom = childView.getBottom() + mDrawable.getIntrinsicHeight();

            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }
}
