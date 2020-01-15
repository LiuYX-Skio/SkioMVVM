package com.skio.base;


import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： LiuYX
 */

public class BaseViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
    /**
     * ViewDataBinding
     */
    private B mBinding;

  
    public BaseViewHolder(B binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    /**
     * @return viewDataBinding
     */
    public B getBinding() {
        return mBinding;
    }

}
