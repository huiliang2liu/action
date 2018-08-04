package com.xh.write;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.xh.entity.ActionEntity;
import com.xh.entity.XmlEntity;
import com.xh.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class BindWrite extends AbsWrite {
    private List<XmlEntity> mEntities;
    private final static String CLICK_METHOD = "public void %s(View v){\n}";
    private final static String INIT_VIEW = "private void initView(){\n%s}";
    private final static String BIND_VIEW = "%s=(%s)findViewById(R.id.%s);\n";
    private final static String CLICK = "%s.setOnClickListener(mOnClickListener);\n";
    private final static String CLICK_NULL = "%s.setOnClickListener(null);\n";
    private final static String FIND_CLICK = "findViewById(R.id.%s).setOnClickListener(mOnClickListener);\n";
    private final static String FIND_CLICK_NULL = "findViewById(R.id.%s).setOnClickListener(null);\n";
    private final static String UN_INIT_VIEW_METHOD = "private void unInitView(){\n%s}";
    private final static String ACTIVITY_ON_CREATE = "@Override\nprotected void onCreate(Bundle savedInstanceState) {\nsuper.onCreate(savedInstanceState);\nsetContentView(R.layout.%s);\ninitView();\n}";
    private final static String ACTIVITY_ON_DESTROY = "@Override\nprotected void onDestroy() {\nsuper.onDestroy();\nunInitView();\n}";
    private final static String FRAGMENT_ON_DESTROY = "@Override\npublic void onDestroy() {\nsuper.onDestroy();\nunInitView();\n}";
    private final static String FRAGMENT_ON_CREATE_VIEW = "@Override\npublic View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {\nif (view == null) {\nview = inflater.inflate(R.layout.%s, container);\ninitView();\n}\nreturn view;\n}";
    private final static String FIND_VIEW_BY_ID = "private View findViewById(int id){\nif(view!=null)\nreturn view.findViewById(id);\nreturn null;\n}";

    public BindWrite(ActionEntity entity, List<XmlEntity> entities) {
        super(entity);
        mEntities = entities;
    }

    @Override
    protected void write() {
        List<XmlEntity> click = new ArrayList<>();//需要设置点击事件的

        for (XmlEntity entity : mEntities) {
            if (entity.id != null && !entity.id.isEmpty()) {
                if (entity.use)
                    Util.createPrivateFiield(mEntity.mFactory, mEntity.mPsiClass, entity.type, entity.field);
                if (entity.method != null && !entity.method.isEmpty()) {
                    mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(CLICK_METHOD, entity.method), mEntity.mPsiClass));
                } else if (entity.click)
                    click.add(entity);
            } else if (entity.method != null && !entity.method.isEmpty()) {
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(CLICK_METHOD, entity.method), mEntity.mPsiClass));
            }


        }

        StringBuffer unInitView = new StringBuffer();
        StringBuffer stringBuffer = new StringBuffer();
        for (XmlEntity entity : mEntities) {
            if (entity.id != null && !entity.id.isEmpty()) {
                if (entity.use) {
                    stringBuffer.append(String.format(BIND_VIEW, entity.field, entity.type, entity.id));
                    if (entity.click && (entity.method == null || entity.method.isEmpty())) {
                        stringBuffer.append(String.format(CLICK, entity.field));
                        unInitView.append("if(").append(entity.field).append("!=null)\n");
                        unInitView.append(String.format(CLICK_NULL, entity.field));
                    }
                    unInitView.append(entity.field).append("=null;\n");
                } else {
                    if (entity.click && (entity.method == null || entity.method.isEmpty())) {
                        stringBuffer.append(String.format(FIND_CLICK, entity.id));
                        unInitView.append(String.format(FIND_CLICK_NULL, entity.id));
                    }
                }
            }
        }

        if (stringBuffer.length() > 0) {
            if (mEntity.mPsiClass.findMethodsByName("initView", false).length <= 0)
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(INIT_VIEW, stringBuffer.toString()), mEntity.mPsiClass));
        }
        if (unInitView.length() > 0) {
            if (mEntity.mPsiClass.findMethodsByName("unInitView", false).length <= 0)
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(UN_INIT_VIEW_METHOD, unInitView.toString()), mEntity.mPsiClass));
        }
        if (click.size() > 0) {
            stringBuffer = new StringBuffer("mOnClickListener=new View.OnClickListener(){\n @Override\n public void onClick(View v) {\nswitch (v.getId()){\n");
            for (XmlEntity entity : click) {
                stringBuffer.append("case R.id.").append(entity.id).append(":\nbreak;\n");
            }
            stringBuffer.append("}\n}\n}");
            Util.createPrivateFiield(mEntity.mFactory, mEntity.mPsiClass, "View.OnClickListener", stringBuffer.toString());
        }
        if (Util.isActivity(mEntity.mPsiClass, mEntity)) {
            PsiMethod[] methods = mEntity.mPsiClass.findMethodsByName("onCreate", false);
            if (methods.length > 0) {
                PsiMethod onCreate = methods[0];
                for (PsiStatement statement : onCreate.getBody().getStatements()) {
                    if (statement.getFirstChild() instanceof PsiMethodCallExpression) {
                        PsiReferenceExpression expression = ((PsiMethodCallExpression) statement.getFirstChild()).getMethodExpression();
                        if (expression.getText().equals("setContentView")) {
                            onCreate.getBody().addAfter(mEntity.mFactory.createStatementFromText("initView();\n", mEntity.mPsiClass), statement);
                        }
                    }
                }
            } else {
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(ACTIVITY_ON_CREATE, mEntity.layout), mEntity.mPsiClass));
            }
            PsiMethod[] destorys = mEntity.mPsiClass.findMethodsByName("onDestroy", false);
            if (destorys.length > 0) {
                PsiMethod destory = destorys[0];
                destory.getBody().add(mEntity.mFactory.createStatementFromText("unInitView();\n", mEntity.mPsiClass));
            } else {
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(ACTIVITY_ON_DESTROY, mEntity.mPsiClass));
            }
        } else if (!Util.isDialog(mEntity.mPsiClass, mEntity)) {
            if (mEntity.mPsiClass.findMethodsByName("findViewById", false).length <= 0)
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(FIND_VIEW_BY_ID, mEntity.mPsiClass));
        }

        if (Util.isAndroidFragment(mEntity.mPsiClass, mEntity) || Util.isV4Fragment(mEntity.mPsiClass, mEntity)) {
            PsiMethod[] onCreates = mEntity.mPsiClass.findMethodsByName("onCreateView", false);
            if (onCreates.length <= 0) {
                Util.createPrivateFiield(mEntity.mFactory, mEntity.mPsiClass, "View", "view");
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(FRAGMENT_ON_CREATE_VIEW, mEntity.layout), mEntity.mPsiClass));
            }

            PsiMethod[] destorys = mEntity.mPsiClass.findMethodsByName("onDestroy", false);
            if (destorys.length > 0) {
                PsiMethod destory = destorys[0];
                destory.getBody().add(mEntity.mFactory.createStatementFromText("unInitView();\n", mEntity.mPsiClass));
            } else {
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(FRAGMENT_ON_DESTROY, mEntity.mPsiClass));
            }
        }
        if (Util.isDialog(mEntity.mPsiClass, mEntity)) {
            PsiMethod[] methods = mEntity.mPsiClass.findMethodsByName("onCreate", false);
            if (methods.length > 0) {
                PsiMethod onCreate = methods[0];
                for (PsiStatement statement : onCreate.getBody().getStatements()) {
                    if (statement.getFirstChild() instanceof PsiMethodCallExpression) {
                        PsiReferenceExpression expression = ((PsiMethodCallExpression) statement.getFirstChild()).getMethodExpression();
                        if (expression.getText().equals("setContentView")) {
                            onCreate.getBody().addAfter(mEntity.mFactory.createStatementFromText("initView();\n", mEntity.mPsiClass), statement);
                        }
                    }
                }
            } else {
                mEntity.mPsiClass.add(mEntity.mFactory.createMethodFromText(String.format(ACTIVITY_ON_CREATE, mEntity.layout), mEntity.mPsiClass));
            }
        }
    }
}
