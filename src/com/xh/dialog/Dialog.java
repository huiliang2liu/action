package com.xh.dialog;

import javax.swing.*;

public class Dialog extends JFrame {
    public Dialog(JPanel panel) {
        setDefaultCloseOperation(2);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void dismiss() {
        setVisible(false);
        dispose();
    }
}
