package com.example.lyx.lweather.CommonAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/18.
 * <p>
 * <p>
 * recyclerview的通用viewholder
 * }
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    //用于缓存已找到的view
    private SparseArray<View> mViews;

    public ViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }


    /**
     * @param viewId itemview中的viewid
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        //先在缓存里查找
        View view = mViews.get(viewId);
        //使用缓存减少findviewbyid的次数
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * TextView控件文本链式调用    holder.setText(xx,xx).setText(xx,xx).setText...;
     */
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        //能够链式调用
        return this;
    }


    /**
     * 设置图片资源
     *
     * @param viewId
     * @param resourceId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }


    /**
     * 设置图片通过路径,这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样glide fresco picasso等
     * <p>
     * 也可以直接写死
     * <p>
     * 使用方法：
     * 新建一个类去继承HolderImageLoader，重写其中的loadImage方法
     * 再放入setImagePath方法
     * <p>
     * 先在继承的adapter中使用或者在外部新建一个类来继承HolderImageLoader
     * public Holderxxx extends HolderImageLoader{
     * public void loadImage(Context context, ImageView imageView, String imagePath){
     * Glide.with(mContext).load(path).placeholder(R.drawable.xx).into(imageView);
     * }
     * <p>
     * }
     * 然后
     * holder.setImagePath(R.id.xx,new Holderxxx(URL path));
     */
    public ViewHolder setImagePath(int viewId, HolderImageLoader imageLoader) {
        ImageView imageView = getView(viewId);
        if (imageLoader == null) {
            throw new NullPointerException("imageLoader is null!");
        }
        imageLoader.loadImage(imageView.getContext(), imageView, imageLoader.getImagePath());


        return this;
    }

    /**
     * 图片加载
     */
    public static abstract class HolderImageLoader {
        private String mImagePath;

        public HolderImageLoader(String imagePath) {
            this.mImagePath = imagePath;
        }

        public String getImagePath() {
            return mImagePath;
        }

        public abstract void loadImage(Context context, ImageView imageView, String imagePath);
    }


    /**
     * 设置View的可见性
     */
    public ViewHolder setViewVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

}


