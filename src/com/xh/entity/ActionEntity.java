package com.xh.entity;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

public class ActionEntity {
    public Project mProject;//当前项目
    public Editor mEditor;//当前编辑器
    public PsiFile mPsiFile;//当前编辑的文件
    public PsiClass mPsiClass;//当前编辑的class文件
    public PsiElementFactory mFactory;//节点工厂
    public SelectionModel mSelectionModel;//当前选择
    public CaretModel mCaretModel;//当前光标
    public Document mDocument;
    public String layout;
    public int lineNumber;//选择的行号
    public int startOffset;//选择开始位置
    public int endOffset;//选择结束位置
    public int maxOffset;//最大插入字符串
    public int nextStartOffset;
    public int insertOffset;
    public GlobalSearchScope scope;
    public PsiFile xmlFile;
    public ActionEntity(AnActionEvent event){
        mProject=event.getData(PlatformDataKeys.PROJECT);
        scope=GlobalSearchScope.allScope(mProject);
        mEditor=event.getData(PlatformDataKeys.EDITOR);
        mPsiFile=event.getData(PlatformDataKeys.PSI_FILE);
        mFactory= JavaPsiFacade.getElementFactory(mProject);
        if(mEditor!=null){
            mDocument=mEditor.getDocument();
            mSelectionModel=mEditor.getSelectionModel();
            if(mSelectionModel!=null){
                layout=mSelectionModel.getSelectedText();
                startOffset=mSelectionModel.getSelectionStart();
                endOffset=mSelectionModel.getSelectionEnd();
                lineNumber=mDocument.getLineNumber(endOffset);
                maxOffset=mDocument.getTextLength()-1;
                nextStartOffset = mDocument.getLineStartOffset(lineNumber + 1);
                insertOffset=maxOffset>nextStartOffset?nextStartOffset:maxOffset;
                PsiFile[] psiFiles = FilenameIndex.getFilesByName(mProject, mSelectionModel.getSelectedText() + ".xml", scope);
                if(psiFiles.length>0)
                    xmlFile=psiFiles[0];
            }
            mCaretModel=mEditor.getCaretModel();
            if(mCaretModel!=null){
                int offset=mCaretModel.getOffset();
                PsiElement element= mPsiFile.findElementAt(offset);
                mPsiClass=PsiTreeUtil.getParentOfType(element,PsiClass.class);
            }

        }

    }
}
