package cj.com.fileexplorer.views;

import java.util.ArrayList;

import cj.com.filemanager.models.FileModel;

/**
 * Created by EpiphX on 1/14/18.
 */

public interface DirectoryView {
    void setDirectoryTitle(String title);
    void showFiles(ArrayList<FileModel> fileModels);
    void clearFiles();
    void navigateToInternalStorage();
    void navigateToExternalStorage();
    void viewFile(FileModel fileModel);

    void showExtendedInformationOnFile(FileModel fileModel);
}
