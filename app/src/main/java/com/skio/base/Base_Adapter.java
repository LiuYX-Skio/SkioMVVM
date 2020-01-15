package com.skio.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： LiuYX
 */

public abstract class Base_Adapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public Context mContext;
    public List<T> mList; // 数据源
    public LayoutInflater inflater;


    public Base_Adapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
        inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateVH(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH vh, int position) {
        onBindVH(vh, position);
    }


    /**
     * 创建 View Holder
     *
     * @param parent   parent
     * @param viewType item type
     * @return view holder
     */
    public abstract VH onCreateVH(ViewGroup parent, int viewType);


    /**
     * 绑定 View Holder
     *
     * @param vh       view holder
     * @param position position
     */
    public abstract void onBindVH(VH vh, int position);



    /**
     * 刷新数据
     *
     * @param data 数据源
     */
    public void refreshData(List<T> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 加载更多
     *
     * @param data 加载的新数据
     */
    public void loadMoreData(List<T> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }
}
