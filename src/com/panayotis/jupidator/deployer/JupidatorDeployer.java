/*
 * JupidatorDeployer.java
 *
 * Created on September 29, 2008, 5:10 PM
 */
package com.panayotis.jupidator.deployer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFrame;

/**
 *
 * @author  teras
 */
public class JupidatorDeployer extends JFrame {

    public static final String EXTENSION = ".jupidator";
    private static BufferedWriter out;
    

    static {
        try {
            String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "jupidator." + new SimpleDateFormat("yyyyMMdd_hhmmss").format(Calendar.getInstance().getTime()) + ".log";
            out = new BufferedWriter(new FileWriter(filename));
        } catch (IOException ex) {
        }
    }

    private static void debug(String message) {
        try {
            if (out != null) {
                out.write(message);
                out.newLine();
                out.flush();
            }
        } catch (IOException ex) {
        }
    }

    private static void endDebug() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void main(String[] args) {
        try {
            new JupidatorDeployer();
            debug("Start log of Jupidator Deployer with arguments:");
            for (int i = 0; i < args.length; i++) {
                debug("  #" + i + ": " + args[i]);
            }

            int files = Integer.valueOf(args[0]);
            debug("Number of affected files: " + files);

            for (int i = 1; i <= files; i++) {
                boolean rm = args[i].charAt(0) == '-';
                String path = args[i].substring(1, args[i].length());
                debug("Working with " + path + "");
                if (rm) {
                    debug("  Deleting file " + path);
                    new File(path).delete();
                } else {
                    String oldpath = path.substring(0, path.length() - EXTENSION.length());
                    File oldfile = new File(oldpath);
                    File newfile = new File(path);

                    debug("  Deleting file " + oldfile);
                    oldfile.delete();
                    debug("  Renaming " + path + " to " + oldfile);
                    newfile.renameTo(oldfile);
                }
                debug("End of works with " + path);
            }

            files++;
            String exec[] = new String[args.length - files];
            debug("Restarting application with following arguments:");
            for (int i = files; i < args.length; i++) {
                exec[i - files] = args[i];
                debug("  #" + (i - files) + ": " + exec[i - files]);
            }
            try {
                Runtime.getRuntime().exec(exec);
            } catch (IOException ex) {
            }

        } catch (Exception ex) {
            debug("Exception found: " + ex.toString());
        } finally {
            endDebug();
            System.exit(0);
        }
    }

    public JupidatorDeployer() {
        super();

        initComponents();
        ProgressBar.putClientProperty("JProgressBar.style", "circular");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        TextL = new javax.swing.JLabel();
        ProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jPanel1.setLayout(new java.awt.BorderLayout(12, 0));

        TextL.setText("Please wait while deploying files");
        jPanel1.add(TextL, java.awt.BorderLayout.WEST);

        ProgressBar.setIndeterminate(true);
        ProgressBar.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(ProgressBar, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar ProgressBar;
    private javax.swing.JLabel TextL;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
