package com.xh.write;

import com.xh.entity.ActionEntity;

public abstract class AbsWrite implements Runnable {
    protected ActionEntity mEntity;
    public AbsWrite(ActionEntity entity){
        mEntity=entity;
    }
    @Override
    public final void run() {
        write();
    }

    protected abstract void write();
}
