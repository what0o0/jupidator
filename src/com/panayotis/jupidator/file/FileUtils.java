/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.gui.BufferListener;
import static com.panayotis.jupidator.i18n.I18N._;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

/**
 *
 * @author teras
 */
public class FileUtils {

    public final static char FS = System.getProperty("file.separator").charAt(0);
    public final static String JAVAHOME = System.getProperty("java.home");
    public final static String JAVABIN = getJavaExec();

    private static String getJavaExec() {
        String EXEC = System.getProperty("os.name").toLowerCase().contains("windows") ? "java.exe" : "java";
        String file;
        file = JAVAHOME + FS + "bin" + FS + EXEC;
        if (new File(file).isFile())
            return file;
        file = JAVAHOME + FS + "jre" + FS + "bin" + FS + EXEC;
        if (new File(file).isFile())
            return file;
        return null;
    }

    public static String copyFile(InputStream in, OutputStream out, BufferListener blisten) {
        String message = null;
        byte[] buffer = new byte[1024];
        int count;

        try {
            while ((count = in.read(buffer)) != -1) {
                if (Thread.interrupted()) {
                    throw new IOException("User asked to cancel update");
                }
                out.write(buffer, 0, count);
                if (blisten != null)
                    blisten.addBytes(count);
            }
        } catch (IOException ex) {
            message = ex.getMessage();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                if (message != null)
                    message = ex.getMessage();
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                if (message != null)
                    message = ex.getMessage();
            }
        }
        return message;
    }

    public static String copyClass(String CLASSNAME, String FILEHOME) {
        String CLASS = CLASSNAME.substring(CLASSNAME.lastIndexOf('.') + 1);
        String CLASSDIR = CLASSNAME.substring(0, CLASSNAME.length() - CLASS.length() - 1).replace('.', '/');

        String CLASSFILE = CLASS + ".class";
        String CLASSPATH = CLASSDIR + "/" + CLASSFILE;
        String CLASSPATHSYSTEM = CLASSPATH.replace('/', FS);

        String FILEDIR = FILEHOME + FS + CLASSDIR.replace('/', FS);
        String FILEOUT = FILEDIR + FS + CLASSFILE;

        StringTokenizer tok = new StringTokenizer(System.getProperty("java.class.path"),
                System.getProperty("path.separator"));

        /* Create Java path */
        File dir = new File(FILEDIR);
        dir.mkdirs();
        if ((!dir.isDirectory()) || (!dir.canWrite()))
            return _("Deployer path {0} is not writable.", dir.getPath());

        /* Find JAR/EXE with the desired .class file */
        String path;
        while (tok.hasMoreTokens()) {
            path = tok.nextToken();
            if (path.length() > 4 && (path.toLowerCase().endsWith(".jar") || path.toLowerCase().endsWith(".exe"))) {
                try {
                    ZipFile zip = new ZipFile(path);
                    if (copyFile(zip.getInputStream(zip.getEntry(CLASSPATH)),
                            new FileOutputStream(FILEOUT), null) == null)
                        return null;
                } catch (IOException ex) {
                }
            } else {
                if (path.length() > 0 && path.charAt(path.length() - 1) != FS)
                    path = path + FS;
                path = path + CLASSPATHSYSTEM;
                try {
                    if (copyFile(new FileInputStream(path), new FileOutputStream(FILEOUT), null) == null)
                        return null;
                } catch (FileNotFoundException ex) {
                }
            }
        }
        return _("Unable to create Deployer");
    }

    static boolean isWritable(File f) {
        if (f == null || f.equals(""))
            throw new NullPointerException(_("Updated file could not be null."));
        if (!isParentWritable(f))
            return false;
        if (!f.exists())
            return true;
        return isWritableLoop(f);
    }

    private static boolean isWritableLoop(File f) {
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++) {
                if (!isWritable(dir[i]))
                    return false;
            }
            return true;
        } else {
            return f.canWrite();
        }
    }

    private static boolean isParentWritable(File f) {
        File p = f.getParentFile();
        if (p == null)  // No parent file - can't work on root files
            return false;
        if (f.exists()) {
            /* we are sure that a parent exists for this file */
            return p.canWrite();
        } else {
            if (p.exists()) {
                /* Check if parent file is directory AND can write in it */
                if (p.isDirectory() && p.canWrite())
                    return true;
                return false;
            } else {
                /* directories created (?) */
                return p.mkdirs();
            }
        }
    }
}
