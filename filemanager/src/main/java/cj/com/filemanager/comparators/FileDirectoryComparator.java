package cj.com.filemanager.comparators;

import java.io.File;
import java.util.Comparator;

/**
 * Created by EpiphX on 1/22/18.
 */

public class FileDirectoryComparator implements Comparator<File> {
    @Override
    public int compare(File fileA, File fileB) {
        if (fileA.isDirectory() && fileB.isDirectory()) {
            return fileA.getName().compareTo(fileB.getName());
        } else if (fileA.isDirectory() && !fileB.isDirectory()) {
            return -1;
        } else if (!fileA.isDirectory() && fileB.isDirectory()) {
            return 1;
        } else {
            return fileA.getName().compareTo(fileB.getName());
        }
    }
}
