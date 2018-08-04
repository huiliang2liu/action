package com.xh.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.search.GlobalSearchScope;
import com.xh.entity.ActionEntity;

public class Util {

    private final static String PRIVATE_FIELD = "private %s %s;\n";
    private final static String PUBLIC_FIELD = "public %s %s;\n";
    private final static String PROTECTED_FIELD = "%s %s;\n";

    public static boolean isActivity(PsiClass cls, ActionEntity entity) {
        return isSuper(cls, name2Class("android.app.Activity", entity.mProject, entity.scope));
    }

    public static boolean isApplication(PsiClass cls, ActionEntity entity) {
        return isSuper(cls, name2Class("android.app.Application", entity.mProject, entity.scope));
    }

    public static boolean isDialog(PsiClass cls, ActionEntity entity) {
        return isSuper(cls, name2Class("android.app.Dialog", entity.mProject, entity.scope));
    }

    public static boolean isAndroidFragment(PsiClass cls, ActionEntity entity) {
        return isSuper(cls, name2Class("android.app.Fragment", entity.mProject, entity.scope));
    }

    public static boolean isV4Fragment(PsiClass cls, ActionEntity entity) {
        return isSuper(cls, name2Class("android.support.v4.app.Fragment", entity.mProject, entity.scope));
    }

    public static boolean isView(PsiClass cls, ActionEntity entity) {
        return isSuper(cls, name2Class("android.view.View", entity.mProject, entity.scope));
    }

    public static PsiClass name2Class(String name, Project project, GlobalSearchScope scope) {
        if (name == null || name.isEmpty() || project == null || scope == null)
            return null;
        return JavaPsiFacade.getInstance(project).findClass(name, scope);
    }

    public static boolean isSuper(PsiClass cls, PsiClass superClass) {
        if (superClass == null)
            return false;
        while (cls != null) {
            if (cls.isInheritor(superClass, false))
                return true;
            cls = cls.getSuperClass();
        }
        return false;
    }

    public static void createPrivateFiield(PsiElementFactory factory, PsiClass cls, String type, String name) {
        if (cls.findFieldByName(name, false) != null)
            return;
        cls.add(factory.createFieldFromText(String.format(PRIVATE_FIELD, type, name), cls));
    }

    public static void createPublicFiield(PsiElementFactory factory, PsiClass cls, String type, String name) {
        if (cls.findFieldByName(name, false) != null)
            return;
        cls.add(factory.createFieldFromText(String.format(PUBLIC_FIELD, type, name), cls));
    }

    public static void createProtectedFiield(PsiElementFactory factory, PsiClass cls, String type, String name) {
        if (cls.findFieldByName(name, false) != null)
            return;
        cls.add(factory.createFieldFromText(String.format(PROTECTED_FIELD, type, name), cls));
    }

}
