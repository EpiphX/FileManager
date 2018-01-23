package cj.com.filemanager.comparators;

import java.util.Comparator;

import cj.com.filemanager.models.FileModel;

public class FileNameComparator implements Comparator<FileModel> {
    @Override
    public int compare(FileModel a, FileModel b) {
        return a.getFile().getName().compareTo(b.getFile().getName());
    }
}
