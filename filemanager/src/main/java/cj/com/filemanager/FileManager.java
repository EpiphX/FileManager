package cj.com.filemanager;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import cj.com.filemanager.models.FileModel;

public class FileManager {
    private static final String TAG = "Files";
    private static final String CURRENT_DIRECTORY_KEY = "CURRENT_DIRECTORY";
    private static final String DIRECTORY_HISTORY_KEY = "DIRECTORY_HISTORY";

    private FileObserver mFileObserver;

    // Stack to keep track of directory navigation. Every time the file manager is informed to
    // navigate to a new directory, this stack should be updated before committing the move.

    private Stack<String> mDirectoryHistory;
    private String mCurrentDirectoryPath;
    private FileManagerListener mListener;

    // Called when either a configuration or low memory change has occurred. Will save off the
    // current directory and the navigation history.
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_DIRECTORY_KEY, mCurrentDirectoryPath);
        outState.putSerializable(DIRECTORY_HISTORY_KEY, mDirectoryHistory);
    }

    // Called when a reload from a saved state has occurred. Will set the current directory and
    // navigation stack to their respective saved values.
    public void onResumeState(Bundle inState) {
        mCurrentDirectoryPath = inState.getString(CURRENT_DIRECTORY_KEY, mCurrentDirectoryPath);
        mDirectoryHistory = (Stack<String>) inState.getSerializable(DIRECTORY_HISTORY_KEY);
    }

    public interface FileManagerListener {
        void directoryHasUpdated();
    }

    public FileManager(FileManagerListener listener) {
        mDirectoryHistory = new Stack<>();
        mListener = listener;
    }

    /**
     * Gets all files for the given directory.
     *
     * @return      All the files for the given directory path.
     */
    public ArrayList<FileModel> getAllFiles() {
        ArrayList<FileModel> fileModels = new ArrayList<>();

        if (TextUtils.isEmpty(mCurrentDirectoryPath)) {
            mCurrentDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        Log.d(TAG, "Path: " + mCurrentDirectoryPath);
        File f = new File(mCurrentDirectoryPath);
        File file[] = f.listFiles();
        if (file != null) {
            Log.d(TAG, "Size: " + file.length);
            for (int i = 0; i < file.length; i++) {
                Log.d(TAG, "FileName:" + file[i].getName());
                Log.d(TAG, "File is directory - " + file[i].isDirectory());
                Log.d(TAG, "File size is - " + file[i].getTotalSpace());

                FileIcon fileIcon;
                if (file[i].isDirectory()) {
                    fileIcon = null;
                } else {
                    fileIcon = new FileIcon(FileUtils.getFileExtension(file[i]));
                }
                fileModels.add(new FileModel(file[i], fileIcon));
            }
        }

        return fileModels;
    }

    public String getCurrentDirectoryPath() {
        return mCurrentDirectoryPath;
    }

    public void navigateToDirectory(String absolutePath) {
        // Before we navigate to the next directory add it to our stack.
        mDirectoryHistory.add(mCurrentDirectoryPath);
        // Update the current directory path to the newest directory.
        mCurrentDirectoryPath = absolutePath;
        // Update the file observer to listen to changes made to the current directory path.
        updateFileObserver();
    }

    private void updateFileObserver() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }
        mFileObserver = new FileObserver(mCurrentDirectoryPath, FileObserver.CREATE |
                FileObserver.MODIFY | FileObserver.MOVED_FROM | FileObserver.MOVED_TO) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if (mListener != null) {
                    mListener.directoryHasUpdated();
                }
            }
        };

        // TODO: Need to fix file observer. It is impacting the performance of the recycler view.
//        mFileObserver.startWatching();
    }

    public boolean navigateToPreviousDirectory() {
        if (mDirectoryHistory.size() != 0) {
            mCurrentDirectoryPath = mDirectoryHistory.pop();
            updateFileObserver();
            return true;
        } else {
            return false;
        }
    }

    public String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getInternalStorageDirectory() {
        return Environment.getRootDirectory().getAbsolutePath();
    }



    // Add ability to interface with external storage.
    // Add ability to get file type.
    // Add ability to get file thumbnail.
    // Add ability for file structure to receive updates when an update has changed.
}
