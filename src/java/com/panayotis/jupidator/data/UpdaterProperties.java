/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author teras
 */
public class UpdaterProperties implements Serializable  {

    private final static String TIMEIGNORE = "Updater.Version.NextCheck";
    private final static String VERSIONIGNORE = "Updater.Version.Ignore";
    private final Properties opts;
    private ApplicationInfo appinfo;

    public UpdaterProperties(ApplicationInfo appinfo) throws UpdaterException {
        opts = new Properties();
        if (appinfo == null)
            return;
        this.appinfo = appinfo;
        try {
            opts.loadFromXML(new FileInputStream(appinfo.getUpdaterConfigFile()));
            opts.remove("Updater.Version.Release");
            opts.remove("Updater.Version.LastCheck");
            storeOptions();
        } catch (IOException ex) {
        }
        appinfo.updateIgnoreRelease(opts.getProperty(VERSIONIGNORE, "0"));
    }

    public boolean isTooSoon() {
        long now = Calendar.getInstance().getTimeInMillis();
        try {
            long last = Long.parseLong(opts.getProperty(TIMEIGNORE, "-1"));
            long next = last + 1000 * 60 * 60 * 24;
            if (now < next)
                return true;
        // It's too soon - We don't need to check it, yet
        } catch (NumberFormatException e) { // if something went wrong, just check web version  
        }
        return false;
    }

    public void defer() {
        opts.put(TIMEIGNORE, Long.toString(Calendar.getInstance().getTimeInMillis()));
        storeOptions();
    }

    public void ignore(int newrelease) {
        opts.put(VERSIONIGNORE, Integer.toString(newrelease));
        storeOptions();
    }

    private void storeOptions() {
        try {
            opts.storeToXML(new FileOutputStream(appinfo.getUpdaterConfigFile()), "Jupidator Java Updater http://sourceforge.net/projects/jupidator/");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to store config file : " + ex.getMessage());
        }
    }
}
