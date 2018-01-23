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
 * Created by EpiphX on 1/14/18.
 */

public class DirectoryPresenter implements FileManager.FileManagerListener {
    private WeakReference<DirectoryView> mDirectoryViewWeakReference;
    private FileManager mFileManager;

    public DirectoryPresenter(DirectoryView directoryView) {
        mDirectoryViewWeakReference = new WeakReference<DirectoryView>(directoryView);
        mFileManager = new FileManager(this);
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

        if (fileModel.getFile().isDirectory()) {
            mFileManager.navigateToDirectory(fileModel.getFile().getAbsolutePath());
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

    public void onNavigateToInternalStorage(Context context) {
        if (mFileManager.getCurrentDirectoryPath().equals(mFileManager
                .getInternalStorageDirectory(context))) {
            return;
        }

        mFileManager.navigateToDirectory(mFileManager.getInternalStorageDirectory(context));
        onFilesRequest();
    }

    public void onLongFileModelPress(FileModel fileModel) {
        DirectoryView directoryView = mDirectoryViewWeakReference.get();

        if (directoryView == null) {
            return;
        }

        directoryView.showExtendedInformationOnFile(fileModel);
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
