package com.xh.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.xh.entity.ActionEntity;
import com.xh.write.AbsWrite;

public abstract class AbsAction extends AnAction {
    private ActionEntity entity;

    @Override
    public final void update(AnActionEvent anActionEvent) {
        super.update(anActionEvent);
        if (gone(anActionEvent))
            anActionEvent.getPresentation().setVisible(false);

    }

    @Override
    public final void actionPerformed(AnActionEvent anActionEvent) {
        entity = new ActionEntity(anActionEvent);
        paras(entity);
    }

    /**
     * 启动写代码
     */
    protected void runWrite() {
        WriteCommandAction.runWriteCommandAction(entity.mProject, write());
    }

    /**
     * 写代码的对象
     *
     * @return
     */
    protected abstract AbsWrite write();

    /**
     * 解析
     *
     * @param entity
     */
    protected abstract void paras(ActionEntity entity);

    /**
     * 设置是否可见
     *
     * @param anActionEvent
     * @return
     */
    protected boolean gone(AnActionEvent anActionEvent) {
        return false;
    }


}
