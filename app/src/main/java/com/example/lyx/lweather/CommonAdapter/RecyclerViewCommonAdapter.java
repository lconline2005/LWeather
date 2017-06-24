package com.example.lyx.lweather.CommonAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/6/18.
 *
 * @param <DATA>
 */

public abstract class RecyclerViewCommonAdapter<DATA> extends RecyclerView.Adapter<ViewHolder> {
    //itemid不一样，通过参数传
    private int mLayoutId;
    protected Context mContext;
    protected List<DATA> mData;
    private LayoutInflater mInflater;
    private MulitiTypeSupprot mTypeSupprot;


    public RecyclerViewCommonAdapter(Context context, List<DATA> Data, int layoutId) {
        mLayoutId = layoutId;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = Data;
    }

    public RecyclerViewCommonAdapter(Context context, List<DATA> Data, MulitiTypeSupprot typeSupport) {
        this(context, Data, -1);
        this.mTypeSupprot = typeSupport;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mTypeSupprot != null) {
            mLayoutId = viewType;
        }
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mTypeSupprot != null) {
            return mTypeSupprot.getLayoutId(mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //ViewHolder优化
        convert(holder, mData.get(position), position);

        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(position);
                }
            });
        }
        if (mItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemLongClick(position);
                }
            });
        }
    }


    /**
     * @param holder
     * @param itemData 当前位置的数据
     * @param position 当前位置
     */
    protected abstract void convert(ViewHolder holder, DATA itemData, int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //点击事件
    private ItemClickListener mItemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    //点击事件
    private ItemLongClickListener mItemLongClickListener;

    public void setOnItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }
}
