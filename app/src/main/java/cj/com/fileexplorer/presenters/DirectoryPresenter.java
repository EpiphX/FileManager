package cj.com.fileexplorer.presenters;

import android.content.Context;
import android.os.Bundle;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
            ArrayList<FileModel> fileModels = mFileManager.getAllFiles();
            // TODO: Look into showing directories at top.
//            Collections.sort(fileModels, new Comparator<FileModel>() {
//                @Override
//                public int compare(FileModel fileModel, FileModel t1) {
//                    if (fileModel.getFile().isDirectory() && t1.getFile().isDirectory()) {
//                        return 0;
//                    } else if (!fileModel.getFile().isDirectory() && t1.getFile().isDirectory()) {
//                        return 1;
//                    } else if (fileModel.getFile().isDirectory() && !t1.getFile().isDirectory()) {
//                        return -1;
//                    } else {
//                        return 0;
//                    }
//                }
//            });
            directoryView.showFiles(fileModels);
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
        onFilesRequest();
    }



}
