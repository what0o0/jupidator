/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.elements.compression.BZip2Compression;
import com.panayotis.jupidator.elements.compression.CompressionMethod;
import com.panayotis.jupidator.elements.compression.GZipCompression;
import com.panayotis.jupidator.elements.compression.NullCompression;
import com.panayotis.jupidator.elements.compression.ZipCompression;
import com.panayotis.jupidator.elements.security.Digester;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.BufferListener;
import jupidator.launcher.XEFile;
import jupidator.launcher.XElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 
 * @author teras
 */
public class ElementFile extends JupidatorElement {

    private static final String EXTENSION = ".jupidator";
    /** This is actually a URL */
    private final String source;
    private final CompressionMethod compression;
    private final ArrayList<Digester> digesters;
    //
    private final URL source_location;
    private final File download_location;
    private final File uncompress_location;

    public ElementFile(String name, String source, String dest, String size, String compress, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, size, elements, info, ExecutionTime.MID);
        if (source == null)
            source = "";
        this.source = info.applyVariables(elements.getBaseURL() + source);

        if (compress == null)
            compress = "none";
        compress = compress.toLowerCase();
        if (compress.equals("zip"))
            compression = new ZipCompression();
        else if (compress.equals("bzip2") || compress.equals("bz2"))
            compression = new BZip2Compression();
        else if (compress.equals("gz") || compress.equals("gzip"))
            compression = new GZipCompression();
        else
            compression = new NullCompression();
        digesters = new ArrayList<Digester>();

        // Find download URL
        URL url = null;
        try {
            url = new URL(getSourceFile() + compression.getFilenameExtension());
        } catch (MalformedURLException ex) {
        }
        source_location = url;

        // Find download location
        if (requiresPrivileges())
            download_location = new File(PermissionManager.manager.requestSlot(), getFileName() + compression.getFilenameExtension() + EXTENSION);
        else
            download_location = new File(getDestinationFile() + compression.getFilenameExtension() + EXTENSION);
        uncompress_location = new File(download_location.getParent(), getFileName() + EXTENSION);
    }

    public boolean exists() {
        return new File(getDestinationFile()).exists();
    }

    public void addDigester(Digester digester) {
        if (digester != null)
            digesters.add(digester);
    }

    private String getSourceFile() {
        return source + "/" + getFileName();
    }

    @Override
    public String toString() {
        return "+" + getSourceFile() + ">" + getDestinationFile();
    }

    public String fetch(UpdatedApplication application, BufferListener watcher) {
        if (source_location == null)
            return _("Unable to initialize URL {0}", getSourceFile());
        if (download_location == null)
            return _("Can not initialize download file {0}", getFileName());

        /* Create destination directory, if it does not exist */
        if (!FileUtils.makeDirectory(download_location.getParentFile()))
            return _("Unable to create directory structure under {0}", download_location.getParentFile().getPath());

        /* Remove old download/uncompressed file, in case it exists */
        if (FileUtils.rmTree(download_location) != null) {
            application.receiveMessage(_("Could not remove old downloaded file {0}", download_location.getPath()));
            return _("Could not remove old downloaded file {0}", download_location.getPath());
        }
        if (FileUtils.rmTree(uncompress_location) != null) {
            application.receiveMessage(_("Could not remove old temporary file {0}", uncompress_location.getPath()));
            return _("Could not remove old downloaded file {0}", uncompress_location.getPath());
        }

        /* Download file */
        String error = null;
        try {
            error = FileUtils.copyFile(source_location.openConnection().getInputStream(),
                    new FileOutputStream(download_location), watcher);
            if (download_location.length() != getSize())
                error = _("Size of file {0} does not match. Reported {1}, required {2}", download_location.getPath(), download_location.length(), getSize());
            else
                for (Digester d : digesters)
                    if (!d.checkFile(download_location)) {
                        error = _("Checksumming {0} with algorithm {1} failed.", download_location, d.getAlgorithm());
                        break;
                    }
        } catch (IOException ex) {
            error = ex.getMessage();
        }

        if (error == null) {
            /* Successfully downloaded file */
            application.receiveMessage(_("File {0} sucessfully downloaded", getFileName()));
            return null;
        } else {
            /* Error while downloading */
            String msg = _("Unable to download file {0}", getFileName());
            application.receiveMessage(msg + " - " + error);
            return msg;
        }
    }

    public String prepare(UpdatedApplication application) {
        String status = compression.decompress(download_location, uncompress_location);
        if (status == null) {
            if (!compression.getFilenameExtension().equals(""))
                if (FileUtils.rmTree(download_location) != null)
                    application.receiveMessage(_("Unable to delete downloaded file {0}", download_location.getPath()));
            return null;
        }
        application.receiveMessage(status);
        return status;
    }

    public void cancel(UpdatedApplication application) {
        String res = FileUtils.rmTree(download_location);
        if (res != null)
            application.receiveMessage(res);
        res = FileUtils.rmTree(uncompress_location);
        if (res != null)
            application.receiveMessage(res);
    }

    @Override
    public XElement getExecElement() {
        File destination = new File(getDestinationFile());
        if (compression.isPackageBased())
            destination = destination.getParentFile();
        return new XEFile(uncompress_location.getPath(), destination.getAbsolutePath());
    }
}
