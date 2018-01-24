package cj.com.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import cj.com.filemanager.models.FileModel;

import static android.os.FileObserver.DELETE;
import static android.os.FileObserver.DELETE_SELF;
import static android.os.FileObserver.MOVED_FROM;
import static android.os.FileObserver.MOVED_TO;
import static android.os.FileObserver.MOVE_SELF;

/**
 * File Manager manages the following responsibilities:
 *
 * It keeps a history of all visited directories in a stack, so that the history can be traversed.
 * It keeps track of the current working directory.
 * It subscribes a {@link FileObserver}, so that notifications of updates to the working
 * directory are received.
 * It provides access to the files of the current working directory.
 *
 */
public class FileManager {
    private static final String TAG = "Files";

    /**
     * Key to save off the current directory into on saved instance state.
     */
    private static final String CURRENT_DIRECTORY_KEY = "CURRENT_DIRECTORY";

    /**
     * Key to save off the directory history into on saved instance state.
     */
    private static final String DIRECTORY_HISTORY_KEY = "DIRECTORY_HISTORY";

    // Serves to receive notifications of updates made to the current viewed directory.
    private FileObserver mFileObserver;

    // Stack to keep track of directory navigation. Every time the file manager is informed to
    // navigate to a new directory, this stack should be updated before committing the move.
    private Stack<String> mDirectoryHistory;

    // Keeps track of the current working directory absolute path.
    private String mCurrentDirectoryPath;

    // Listener to changes to the file manager's directory stack.
    private FileManagerListener mListener;

    // Keeps a weak reference to the context, so that the final manager can use it to get the
    // internal storage directory.
    private WeakReference<Context> mContextWeakReference;

    /**
     * Called when either a configuration or low memory change has occurred. Will save off the
     * current directory and the navigation history into the outState.
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_DIRECTORY_KEY, mCurrentDirectoryPath);
        outState.putSerializable(DIRECTORY_HISTORY_KEY, mDirectoryHistory);
    }

    /**
     * Called when a reload from a saved state has occurred. Will set the current directory and
     * navigation stack to their respective saved values in the inState.
     * @param inState
     */
    public void onResumeState(Bundle inState) {
        mCurrentDirectoryPath = inState.getString(CURRENT_DIRECTORY_KEY, mCurrentDirectoryPath);
        mDirectoryHistory = (Stack<String>) inState.getSerializable(DIRECTORY_HISTORY_KEY);
    }

    /**
     * Upon activity or fragment destruction, this method allows for the FileManager to be
     * commanded to close.
     *
     * When commanded to close it will close any running subscriptions such as the FileObserver
     * in order to help prevent memory leaks.
     */
    public void close() {
        // Close any listeners.
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }
    }

    /**
     * Listener interface to events that occur within the FileManager.
     */
    public interface FileManagerListener {
        /**
         * Informs the listener that the directory has updated.
         */
        void directoryHasUpdated();
    }

    public FileManager(Context context) {
        mDirectoryHistory = new Stack<>();
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public FileManager(Context context, FileManagerListener listener) {
        mDirectoryHistory = new Stack<>();
        mListener = listener;

        mContextWeakReference = new WeakReference<Context>(context);
    }

    public void setListener(FileManagerListener listener) {
        mListener = listener;
    }

    /**
     * Gets all files for the current directory.
     *
     * @param fileComparator       A file comparator can be provided to specify a sorting
     *                             algorithm that should be applied to the returned files.
     * @return
     */
    public ArrayList<FileModel> getAllFilesForCurrentDirectory(@Nullable Comparator<File> fileComparator) {
        ArrayList<FileModel> fileModels = new ArrayList<>();

        if (TextUtils.isEmpty(mCurrentDirectoryPath)) {
            mCurrentDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        Log.d(TAG, "Path: " + mCurrentDirectoryPath);
        File f = new File(mCurrentDirectoryPath);
        File file[] = f.listFiles();

        // It will return a list of all the files for the given directory
        if (fileComparator != null) {
            Arrays.sort(file, fileComparator);
        }

        if (file != null) {
            for (File aFile : file) {

                FileIcon fileIcon;
                if (aFile.isDirectory()) {
                    fileIcon = null;
                } else {
                    fileIcon = new FileIcon(FileUtils.getFileExtension(aFile));
                }
                fileModels.add(new FileModel(aFile, fileIcon));
            }
        }

        return fileModels;
    }

    /**
     * Gets the number of files in the current working directory.
     *
     * @return      Number of files in the directory.
     *              0 if no files are found.
     */
    public int getNumberOfFilesInCurrentDirectory() {
        if (TextUtils.isEmpty(mCurrentDirectoryPath)) {
            return 0;
        }

        int fileCount = 0;

        File f = new File(mCurrentDirectoryPath);
        File file[] = f.listFiles();

        if (file != null) {
            for (File aFile : file) {
                if (aFile.isFile()) {
                    fileCount++;
                }
            }
        }

        return fileCount;
    }

    /**
     * Gets the number of directories in the current working directory.
     *
     * @return      Number of directories in the directory.
     *              0 if no directories are found.
     */
    public int getNumberOfDirectoriesInCurrentDirectory() {
        if (TextUtils.isEmpty(mCurrentDirectoryPath)) {
            return 0;
        }

        int directoryCount = 0;

        File f = new File(mCurrentDirectoryPath);
        File file[] = f.listFiles();

        if (file != null) {
            for (File aFile : file) {
                if (aFile.isDirectory()) {
                    directoryCount++;
                }
            }
        }

        return directoryCount;
    }

    /**
     * Gets the current working directory path.
     */
    public String getCurrentDirectoryPath() {
        return mCurrentDirectoryPath;
    }

    /**
     * Navigates to the given directory path.
     *
     * @param absolutePath
     */
    public void navigateToDirectory(String absolutePath) {
        // Before we navigate to the next directory add it to our stack.
        mDirectoryHistory.add(mCurrentDirectoryPath);
        // Update the current directory path to the newest directory.
        mCurrentDirectoryPath = absolutePath;
        // Update the file observer to listen to changes made to the current directory path.
        updateFileObserver();
    }

    /**
     * Updates the file observer with the current working directory.
     *
     * If a file observer is currently running, then it will first stop the subscription.
     */
    private void updateFileObserver() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }

        /**
         * The FileObserver will listen to all create, moved to, delete, moved from, delete self,
         * and moved self events made to the working directory.
         */
        final int MASK = FileObserver.CREATE | MOVED_TO | DELETE | MOVED_FROM | DELETE_SELF |
                MOVE_SELF;

        mFileObserver = new FileObserver(mCurrentDirectoryPath, MASK) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                // Inform the listener of the FileManager of any updates that fit our MASK.
                if (mListener != null) {
                    mListener.directoryHasUpdated();
                }
            }
        };

        // Start watching. It is important to stop watching on destruction of the activity or view.
        mFileObserver.startWatching();
    }

    /**
     * Navigates to the previous directory in the directory's history.
     *
     * @return      True if a navigation to a previous directory was performed.
     *              False if otherwise.
     */
    public boolean navigateToPreviousDirectory() {
        if (mDirectoryHistory.size() != 0) {
            mCurrentDirectoryPath = mDirectoryHistory.pop();
            updateFileObserver();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the environments external storage directory path.
     */
    public String getExternalStorageDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Gets the internal storage for the application.
     *
     * @return      The internal storage if the context is not null.
     *              Empty if the context has been released from memory.
     */
    public String getInternalStorageDirectoryPath() {
        Context context = mContextWeakReference.get();

        if (context != null) {
            return context.getFilesDir().getAbsolutePath();
        } else {
            return "";
        }
    }
}
