package cj.com.fileexplorer.views;

import android.content.Context;

import java.util.ArrayList;

import cj.com.filemanager.models.FileModel;
import cj.com.fileexplorer.presenters.DirectoryPresenter;

/**
 * View that serves to display directories and their contents.
 *
 * Paired with a directory presenter {@link DirectoryPresenter}
 */
public interface DirectoryView {
    void changeGridToList();
    void changeListToGrid();
    void setDirectoryTitle(String title);
    void showNumberOfDirectories(int numberOfDirectories);
    void showNumberOfFiles(int numberOfFiles);
    void showFiles(ArrayList<FileModel> fileModels);
    void navigateToInternalStorage(Context context);
    void navigateToExternalStorage();
    void viewFile(FileModel fileModel);
    void showExtendedInformationOnFile(FileModel fileModel);
    void directoryHasUpdated();
}
