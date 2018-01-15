package cj.com.filemanager.models;

import java.io.File;

import cj.com.filemanager.FileIcon;

/**
 * Created by EpiphX on 1/14/18.
 */

public class FileModel {
    private File mFile;
    private FileIcon mFileIcon;

    public FileModel(File file, FileIcon fileIcon) {
        mFile = file;
        mFileIcon = fileIcon;
    }

    public File getFile() {
        return mFile;
    }

    public FileIcon getFileIcon() {
        return mFileIcon;
    }
}
