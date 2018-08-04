package com.xh.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.xh.dialog.Dialog;
import com.xh.entity.ActionEntity;
import com.xh.entity.XmlEntity;
import com.xh.listen.ClickListen;
import com.xh.utils.Constant;
import com.xh.utils.MessageUtil;
import com.xh.view.BindIdView;
import com.xh.write.AbsWrite;
import com.xh.write.BindWrite;

import java.util.ArrayList;
import java.util.List;


public class BindId extends AbsAction implements ClickListen {
    private Dialog dialog;
    private ActionEntity entity;
    private List<XmlEntity> entities;

    @Override
    protected void paras(ActionEntity entity) {
        this.entity = entity;
        if (entity.layout == null || entity.layout.isEmpty()) {
            MessageUtil.err("没有选择布局");
            return;
        }
        if (entity.xmlFile == null) {
            MessageUtil.err("没有找到布局" + entity.mSelectionModel.getSelectedText() + ".xml请确认布局是否存在");
            return;
        }
        entities = parasXml(entity, entity.xmlFile);
        if (entities == null || entities.size() <= 0) {
            MessageUtil.err("没有需要绑定的控件");
            return;
        }

        dialog = new Dialog(new BindIdView(this, entities));
    }

    public List<XmlEntity> parasXml(ActionEntity entity, PsiFile xml) {
        List<XmlEntity> entities = new ArrayList<>();
        xml.accept(new XmlRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                if (!(element instanceof XmlTag))
                    return;
                XmlTag tag = (XmlTag) element;

                if (tag.getName().equalsIgnoreCase("include")) {//include标签
                    XmlAttribute layout = tag.getAttribute("layout", null);
                    if (layout != null) {
                        String xmlName = layout.getValue();
                        if (xmlName != null && xmlName.startsWith("@layout/")) {
                            PsiFile[] files = FilenameIndex.getFilesByName(entity.mProject, xmlName.substring(8) + ".xml", entity.scope);
                            if (files.length > 0)
                                entities.addAll(parasXml(entity, files[0]));
                            else
                                MessageUtil.err("没找到布局" + xmlName.substring(8));
                        }
                    }
                    return;
                }
                XmlAttribute idAttribute = tag.getAttribute("android:id", null);
                XmlEntity xmlEntity = null;
                if (idAttribute != null) {
                    String id = idAttribute.getValue();
                    if (id != null && id.startsWith("@+id/")) {
                        xmlEntity = new XmlEntity();
                        xmlEntity.id = id.substring(5);
                        xmlEntity.field = "m";
                        String[] strings = xmlEntity.id.split("_");
                        for (String s : strings) {
                            xmlEntity.field += s.substring(0, 1).toUpperCase() + s.substring(1);
                        }
                        String name = tag.getName();
                        String[] names = name.split("\\.");
                        if (names.length > 1) {//控件全名称
                            xmlEntity.type = names[names.length - 1];
                            xmlEntity.packageName = names[0];
                            for (int i = 1; i < names.length - 1; i++) {
                                xmlEntity.packageName += "." + names[i];
                            }
                        } else {
                            xmlEntity.type = name;
                            xmlEntity.packageName = Constant.paths.containsKey(name) ? Constant.paths.get(name) : "android.widget";
                        }
                    }
                }
                XmlAttribute onClickAttribute = tag.getAttribute("android:onClick", null);
                if (onClickAttribute != null) {
                    String method = onClickAttribute.getValue();
                    if (method != null) {
                        if (xmlEntity == null)
                            xmlEntity = new XmlEntity();
                        xmlEntity.method = method;
                    }
                }
                if (xmlEntity != null)
                    entities.add(xmlEntity);
            }
        });
        return entities;
    }

    @Override
    protected boolean gone(AnActionEvent anActionEvent) {
        ActionEntity actionEntity = new ActionEntity(anActionEvent);
        if (actionEntity.mPsiClass == null)
            return true;
        return super.gone(anActionEvent);
    }

    @Override
    public void cancle() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    protected AbsWrite write() {
        return new BindWrite(entity, entities);
    }

    @Override
    public void confim() {
        if (dialog != null)
            dialog.dismiss();
        runWrite();
    }

    private boolean view(PsiClass cls, Project project) {
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        PsiClass activity = JavaPsiFacade.getInstance(project).findClass("android.app.Activity", searchScope);
        PsiClass androidFrame = JavaPsiFacade.getInstance(project).findClass("", searchScope);
        PsiClass v4Frame = JavaPsiFacade.getInstance(project).findClass("", searchScope);
        PsiClass veiw = JavaPsiFacade.getInstance(project).findClass("", searchScope);
        PsiClass dialog = JavaPsiFacade.getInstance(project).findClass("", searchScope);
        return false;
    }
}
