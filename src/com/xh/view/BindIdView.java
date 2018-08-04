package com.xh.view;

import com.xh.entity.XmlEntity;
import com.xh.listen.ClickListen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class BindIdView extends JPanel {
    private AllListen allListen = new AllListen();
    private Listen listen = new Listen();
    List<Element> elements;
    Heard heard;
    ClickListen clickListen;


    public BindIdView(ClickListen listen, List<XmlEntity> entities) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        clickListen = listen;
        heard = new Heard();
        add(heard);
        elements = new ArrayList<>();
        for (XmlEntity entity : entities) {
            if(entity.id==null)
                continue;
            Element element = new Element(entity);
            add(element);
            elements.add(element);
        }
        add(new commit());
    }

    private class commit extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            JButton concle = new JButton("concle");
            concle.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clickListen != null)
                        clickListen.cancle();
                }
            });
            add(concle);
            JButton confim = new JButton("confim");
            confim.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (clickListen != null)
                        clickListen.confim();
                }
            });
            add(confim);
        }
    }

    private class Element extends JPanel {
        XmlEntity entity;
        JCheckBox use;
        JLabel type;
        JLabel packageName;
        JCheckBox click;
        JTextField filed;
        String name;
        Listen listen = BindIdView.this.listen;

        private Element(XmlEntity xmlEntity) {
            entity = xmlEntity;
            name = entity.field;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setMaximumSize(new Dimension(32767, 54));
            use = new JCheckBox();
            use.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (listen != null)
                        listen.click();
                    entity.use = use.isSelected();
                }
            });
            use.setMinimumSize(new Dimension(10, 10));
            use.setSelected(true);
            add(use);
            add(Box.createRigidArea(new Dimension(5, 0)));
            type = new JLabel(entity.type);
            type.setPreferredSize(new Dimension(120, 30));
            add(type);
            add(Box.createRigidArea(new Dimension(5, 0)));
            packageName = new JLabel(entity.packageName);
            packageName.setPreferredSize(new Dimension(240, 30));
            add(packageName);
            add(Box.createRigidArea(new Dimension(5, 0)));
            click = new JCheckBox();
            click.setMinimumSize(new Dimension(10, 10));
            click.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    entity.click = click.isSelected();
                }
            });
            add(click);
            filed = new JTextField(entity.field);
            filed.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {

                }

                @Override
                public void removeUpdate(DocumentEvent e) {

                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    String string = filed.getText().trim();
                    if (string.isEmpty()) {
                        filed.setText(name);
                        return;
                    } else
                        entity.field = string;
                }
            });
            add(filed);
            add(Box.createHorizontalGlue());
        }

    }

    private class Heard extends JPanel {
        JCheckBox checkBox;//全选
        JLabel type;//类型
        JLabel packageName;
        JLabel filed;//字段
        AllListen listen = BindIdView.this.allListen;

        {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            checkBox = new JCheckBox();
            checkBox.setSelected(true);
            checkBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (listen != null)
                        listen.all(checkBox.isSelected());
                }
            });
            checkBox.setMinimumSize(new Dimension(10, 10));
            add(checkBox);
            add(Box.createRigidArea(new Dimension(5, 0)));
            type = new JLabel("类型");
            type.setPreferredSize(new Dimension(120, 30));
            add(type);
            add(Box.createRigidArea(new Dimension(5, 0)));
            packageName = new JLabel("包名");
            packageName.setPreferredSize(new Dimension(240, 30));
            add(packageName);
            add(Box.createRigidArea(new Dimension(15, 0)));
            filed = new JLabel("字段");
            filed.setPreferredSize(new Dimension(60, 30));
            add(filed);
            add(Box.createHorizontalGlue());
        }
    }

    private class Listen {
        void click() {
            if (heard == null)
                return;
            heard.listen = null;
            boolean choose = true;
            for (Element element : elements) {
                choose &= element.use.isSelected();
            }
            heard.checkBox.setSelected(choose);
            heard.listen = allListen;
        }
    }

    private class AllListen {
        void all(boolean choose) {
            if (elements == null || elements.size() <= 0)
                return;
            for (Element element : elements) {
                element.listen = null;
                element.use.setSelected(choose);
                element.setEnabled(choose);
                element.entity.use = choose;
                element.listen = listen;
            }
        }
    }
}
