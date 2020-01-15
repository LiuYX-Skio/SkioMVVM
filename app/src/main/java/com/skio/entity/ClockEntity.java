package com.skio.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ClockEntity extends BaseObservable {
    private String name;
    private Long time;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
