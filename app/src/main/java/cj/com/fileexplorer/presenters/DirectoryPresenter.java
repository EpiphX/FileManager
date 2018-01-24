package cj.com.fileexplorer.presenters;

import android.content.Context;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cj.com.fileexplorer.views.DirectoryView;
import cj.com.filemanager.FileManager;
import cj.com.filemanager.comparators.FileDirectoryComparator;
import cj.com.filemanager.models.FileModel;

/**
 * Manages the business logic of interaction with the {@link DirectoryView}. It communicates with
 * the {@link FileManager} in order to navigate the directory tree as specified.
 */
public class DirectoryPresenter implements FileManager.FileManagerListener {
    private WeakReference<DirectoryView> mDirectoryViewWeakReference;
    private FileManager mFileManager;

    public DirectoryPresenter(DirectoryView directoryView, FileManager fileManager) {
        mDirectoryViewWeakReference = new WeakReference<DirectoryView>(directoryView);
        mFileManager = fileManager;
        mFileManager.setListener(this);
    }

    public void onFilesRequest() {
        DirectoryView directoryView = mDirectoryViewWeakReference.get();

        if (directoryView != null) {
            ArrayList<FileModel> fileModels = mFileManager.getAllFilesForCurrentDirectory(new
                    FileDirectoryComparator());
            directoryView.showFiles(fileModels);
            directoryView.setDirectoryTitle(mFileManager.getCurrentDirectoryPath());
            directoryView.showNumberOfDirectories(mFileManager.getNumberOfDirectoriesInCurrentDirectory());
            directoryView.showNumberOfFiles(mFileManager.getNumberOfFilesInCurrentDirectory());
        }
    }

    public void onShortFileModelPress(FileModel fileModel) {
        DirectoryView directoryView = mDirectoryViewWeakReference.get();

        if (directoryView == null) {
            return;
        }

        if (fileModel.isDirectory()) {
            mFileManager.navigateToDirectory(fileModel.getAbsolutePath());
            onFilesRequest();
        } else {
            directoryView.viewFile(fileModel);
        }
    }

    public void onNavigateToExternalStorage() {
        if (mFileManager.getCurrentDirectoryPath().equals(mFileManager
                .getExternalStorageDirectory())) {
            return;
        }

        mFileManager.navigateToDirectory(mFileManager.getExternalStorageDirectory());
        onFilesRequest();
    }

    public void onNavigateToInternalStorage() {
        if (mFileManager.getCurrentDirectoryPath().equals(mFileManager
                .getInternalStorageDirectory())) {
            return;
        }

        mFileManager.navigateToDirectory(mFileManager.getInternalStorageDirectory());
        onFilesRequest();
    }

    public void onLongFileModelPress(FileModel fileModel) {
        DirectoryView directoryView = mDirectoryViewWeakReference.get();

        if (directoryView == null) {
            return;
        }

        directoryView.showExtendedInformationOnFile(fileModel);
    }

    public void onDestroy() {
        mFileManager.close();
    }

    public boolean onBackPressed() {
        if (mFileManager.navigateToPreviousDirectory()) {
            onFilesRequest();
            return true;
        } else {
            return false;
        }
    }


    public void onSaveInstanceState(Bundle outState) {
        mFileManager.onSaveInstanceState(outState);
    }

    public void onResumeState(Bundle savedInstanceState) {
        mFileManager.onResumeState(savedInstanceState);
    }

    @Override
    public void directoryHasUpdated() {
        DirectoryView directoryView = mDirectoryViewWeakReference.get();

        if (directoryView == null) {
            return;
        }

        directoryView.directoryHasUpdated();
    }
}
