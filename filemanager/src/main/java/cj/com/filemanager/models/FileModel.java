package cj.com.filemanager.models;

import java.io.File;

import cj.com.filemanager.FileIcon;

/**
 * Model that contains a file and its respective file icon.
 *
 * @see FileIcon for how a files icon is specified.
 * @see File
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
