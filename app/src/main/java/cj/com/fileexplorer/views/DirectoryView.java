package cj.com.fileexplorer.views;

import android.content.Context;

import java.util.ArrayList;

import cj.com.filemanager.models.FileModel;

/**
 * Created by EpiphX on 1/14/18.
 */

public interface DirectoryView {
    void changeGridToList();
    void changeListToGrid();
    void setDirectoryTitle(String title);
    void showFiles(ArrayList<FileModel> fileModels);
    void navigateToInternalStorage(Context context);
    void navigateToExternalStorage();
    void viewFile(FileModel fileModel);
    void showExtendedInformationOnFile(FileModel fileModel);

    void directoryHasUpdated();
}
