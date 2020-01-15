package com.skio.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.skio.BR;
import com.skio.R;
import com.skio.base.BaseViewHolder;
import com.skio.base.Base_Adapter;
import com.skio.entity.ClockEntity;


public class AlarmClockAdapter extends Base_Adapter<ClockEntity, BaseViewHolder> {

    public AlarmClockAdapter(Context context) {
        super(context);
    }


    @Override
    public BaseViewHolder onCreateVH(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_clock, parent, false);
        return new BaseViewHolder(dataBinding);
    }



    @Override
    public void onBindVH(BaseViewHolder viewHolder, int position) {
        ViewDataBinding binding = viewHolder.getBinding();
        binding.setVariable(BR.clock, mList.get(position));
        binding.setVariable(BR.position,position);
        binding.setVariable(BR.adapter,this);
        binding.executePendingBindings(); //防止闪烁
    }


    /**
     * 点击Item更新数据
     *
     * @param clockEntity
     * @param position
     */
    public void clickClock(ClockEntity clockEntity, int position) {
        clockEntity.setName("改变后的闹钟名称"+position);
        notifyItemChanged(position);
    }




}
