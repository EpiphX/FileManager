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
    /**
     * Shows the directories contents in a grid layout.
     */
    void changeGridToList();

    /**
     * Shows the directories contents in a list layout.
     */
    void changeListToGrid();

    /**
     * Sets the directory's title.
     *
     * @param title     The title to showcase.
     */
    void setDirectoryTitle(String title);

    /**
     * Shows the number of directories contained within the active directory.
     *
     * @param numberOfDirectories       The number of directories to show.
     */
    void showNumberOfDirectories(int numberOfDirectories);

    /**
     * Shows the number of files contained within the active directory.
     *
     * @param numberOfFiles             The number of files to show.
     */
    void showNumberOfFiles(int numberOfFiles);

    /**
     * Shows the list of file models to the user.
     * @param fileModels
     */
    void showFiles(ArrayList<FileModel> fileModels);

    /**
     * Navigates to the internal storage of the app.
     *
     * @param context
     */
    void navigateToInternalStorage(Context context);

    /**
     * Navigates to the external storage.
     */
    void navigateToExternalStorage();

    /**
     * Views the given file.
     *
     * @param fileModel
     */
    void viewFile(FileModel fileModel);

    /**
     * Shows extended information on the file.
     * @param fileModel
     */
    void showExtendedInformationOnFile(FileModel fileModel);

    /**
     * Informs the directory view that the directory has updated its contents.
     */
    void directoryHasUpdated();
}
