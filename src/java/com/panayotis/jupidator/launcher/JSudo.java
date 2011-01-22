/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JSudo.java
 *
 * Created on Nov 30, 2009, 6:10:46 PM
 */
package com.panayotis.jupidator.launcher;

import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 *
 * @author teras
 */
public class JSudo extends JDialog {

    private String pass = null;

    /** Creates new form JSudo */
    public JSudo() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Viewport = new javax.swing.JPanel();
        LowerPanel = new javax.swing.JPanel();
        ButtonPanel = new javax.swing.JPanel();
        AllowB = new javax.swing.JButton();
        DenyB = new javax.swing.JButton();
        CentralPanel = new javax.swing.JPanel();
        InfoLabel = new javax.swing.JLabel();
        MsgLabel = new javax.swing.JLabel();
        PassPanel = new javax.swing.JPanel();
        LeftPassPanel = new javax.swing.JPanel();
        PLabel = new javax.swing.JLabel();
        RightPassPanel = new javax.swing.JPanel();
        Password = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CaféPorts authorization");
        setLocationByPlatform(true);
        setModal(true);
        setResizable(false);

        Viewport.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 24, 12, 24));
        Viewport.setLayout(new java.awt.BorderLayout());

        LowerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        LowerPanel.setLayout(new java.awt.BorderLayout());

        ButtonPanel.setLayout(new java.awt.GridLayout(1, 0));

        AllowB.setText("Allow");
        AllowB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AllowBActionPerformed(evt);
            }
        });
        ButtonPanel.add(AllowB);

        DenyB.setText("Deny");
        DenyB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DenyBActionPerformed(evt);
            }
        });
        ButtonPanel.add(DenyB);

        LowerPanel.add(ButtonPanel, java.awt.BorderLayout.EAST);

        Viewport.add(LowerPanel, java.awt.BorderLayout.PAGE_END);

        CentralPanel.setLayout(new java.awt.BorderLayout());

        InfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/panayotis/jupidator/launcher/lock.png"))); // NOI18N
        InfoLabel.setText("Application requires your password.");
        InfoLabel.setIconTextGap(20);
        CentralPanel.add(InfoLabel, java.awt.BorderLayout.NORTH);

        MsgLabel.setText(" ");
        CentralPanel.add(MsgLabel, java.awt.BorderLayout.PAGE_END);

        PassPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 4, 0));
        PassPanel.setLayout(new java.awt.BorderLayout(12, 0));

        LeftPassPanel.setLayout(new java.awt.GridLayout(1, 1, 0, 4));

        PLabel.setText("Password");
        LeftPassPanel.add(PLabel);

        PassPanel.add(LeftPassPanel, java.awt.BorderLayout.WEST);

        RightPassPanel.setLayout(new java.awt.GridLayout(1, 1, 0, 4));

        Password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PasswordActionPerformed(evt);
            }
        });
        RightPassPanel.add(Password);

        PassPanel.add(RightPassPanel, java.awt.BorderLayout.CENTER);

        CentralPanel.add(PassPanel, java.awt.BorderLayout.CENTER);

        Viewport.add(CentralPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(Viewport, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AllowBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AllowBActionPerformed
        setEnabled(false);
        MsgLabel.setForeground(Color.BLACK);
        MsgLabel.setText("Validating...");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                String newp = new String(Password.getPassword());
                if (LaunchManager.testSudo(newp)) {
                    pass = newp;
                    MsgLabel.setForeground(Color.BLACK);
                    MsgLabel.setText(" ");
                    setVisible(false);
                    dispose();
                } else {
                    Password.requestFocus();
                    Password.selectAll();
                    MsgLabel.setText("Wrong password!");
                    MsgLabel.setForeground(Color.RED);
                }
                setEnabled(true);
            }
        });
}//GEN-LAST:event_AllowBActionPerformed

    private void DenyBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DenyBActionPerformed
        pass = null;
        MsgLabel.setForeground(Color.BLACK);
        MsgLabel.setText(" ");
        setVisible(false);
        dispose();
}//GEN-LAST:event_DenyBActionPerformed

    private void PasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasswordActionPerformed
        AllowBActionPerformed(evt);
    }//GEN-LAST:event_PasswordActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AllowB;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JPanel CentralPanel;
    private javax.swing.JButton DenyB;
    private javax.swing.JLabel InfoLabel;
    private javax.swing.JPanel LeftPassPanel;
    private javax.swing.JPanel LowerPanel;
    private javax.swing.JLabel MsgLabel;
    private javax.swing.JLabel PLabel;
    private javax.swing.JPanel PassPanel;
    private javax.swing.JPasswordField Password;
    private javax.swing.JPanel RightPassPanel;
    private javax.swing.JPanel Viewport;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setEnabled(boolean status) {
        super.setEnabled(status);
        PLabel.setEnabled(status);
        Password.setEnabled(status);
        AllowB.setEnabled(status);
        DenyB.setEnabled(status);
    }

    public String getUserPass() {
        return pass;
    }

    @Override
    public void setVisible(boolean status) {
        super.setVisible(status);
        Password.requestFocus();
        Password.selectAll();
    }
}
