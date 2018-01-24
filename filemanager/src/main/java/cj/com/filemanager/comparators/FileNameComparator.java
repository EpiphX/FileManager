package cj.com.filemanager.comparators;

import java.util.Comparator;

import cj.com.filemanager.models.FileModel;

/**
 * Comparator that will sort all files by name alphabetically.
 */
public class FileNameComparator implements Comparator<FileModel> {
    @Override
    public int compare(FileModel a, FileModel b) {
        return a.getFile().getName().compareTo(b.getFile().getName());
    }
}
