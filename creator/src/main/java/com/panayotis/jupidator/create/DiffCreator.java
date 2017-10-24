/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.create;

import com.panayotis.jupidator.parsables.ParseFile;
import com.panayotis.jupidator.parsables.ParseFolder;
import com.panayotis.jupidator.parsables.ParseItem;
import java.io.File;
import java.util.Collection;
import java.util.TreeSet;

/**
 *
 * @author teras
 */
public class DiffCreator extends CommandCreator {

    public static Collection<Command> create(ParseFolder oldInstallation, ParseFolder newInstallation, File inputRoot, File output, String version, String arch, boolean nomd5, boolean nosha1, boolean nosha256, boolean skipfiles) {
        DiffCreator diff = new DiffCreator(inputRoot, output, version, arch, nomd5, nosha1, nosha256, skipfiles);
        diff.diff(oldInstallation, newInstallation, "");
        return diff.getCommands();
    }

    public DiffCreator(File inputRoot, File output, String version, String arch, boolean nomd5, boolean nosha1, boolean nosha256, boolean skipfiles) {
        super(inputRoot, output, version, arch, nomd5, nosha1, nosha256, skipfiles);
    }

    private void diff(ParseItem oldItem, ParseItem newItem, String path) {
        if (newItem == null)
            rm(oldItem, path);
        else if (oldItem == null)
            file(newItem, path);
        else if (!oldItem.getClass().equals(newItem.getClass())) {
            rm(oldItem, path);
            file(newItem, path);
        } else if (oldItem instanceof ParseFile) {
            if (!oldItem.equals(newItem))
                file(newItem, path);
        } else if (oldItem instanceof ParseFolder) {
            path = oldItem.name.equals(".") ? path : path + oldItem.name + "/";

            Collection<String> oldNames = ((ParseFolder) oldItem).names();
            Collection<String> newNames = ((ParseFolder) newItem).names();

            Collection<String> onlyInOld = new TreeSet<>(oldNames);
            onlyInOld.removeAll(newNames);

            Collection<String> onlyInNew = new TreeSet<>(newNames);
            onlyInNew.removeAll(oldNames);

            for (String name : onlyInOld)
                diff(((ParseFolder) oldItem).searchFor(name), null, path);
            for (String name : newNames)
                if (onlyInNew.contains(name))
                    diff(null, ((ParseFolder) newItem).searchFor(name), path);
                else
                    diff(((ParseFolder) oldItem).searchFor(name), ((ParseFolder) newItem).searchFor(name), path);
        }
    }

}
