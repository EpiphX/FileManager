package cj.com.fileexplorer.presenters;

import android.os.Bundle;

import java.lang.ref.WeakReference;

import cj.com.fileexplorer.views.DirectoryView;
import cj.com.filemanager.FileManager;
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
            directoryView.showFiles(mFileManager.getAllFiles());
            directoryView.setDirectoryTitle(mFileManager.getCurrentDirectoryPath());
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
        onFilesRequest();
    }



}
