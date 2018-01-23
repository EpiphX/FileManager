package cj.com.fileexplorer.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cj.com.fileexplorer.BuildConfig;
import cj.com.fileexplorer.R;
import cj.com.fileexplorer.adapters.DirectoryAdapter;
import cj.com.fileexplorer.broadcast_receivers.ListToGridBroadcastReceiver;
import cj.com.fileexplorer.broadcast_receivers.NavigateBroadcastReceiver;
import cj.com.fileexplorer.presenters.DirectoryPresenter;
import cj.com.filemanager.models.FileModel;

import static cj.com.filemanager.FileUtils.getMimeType;

/**
 * Created by EpiphX on 1/14/18.
 */

public class DirectoryFragment extends BaseFragment implements DirectoryView, DirectoryAdapter
        .OnItemClickListener {
    // Request code for permissions from
    public static final int PERMISSION_EXTERNAL_STORAGE_REQUEST_CODE = 100;

    public static final String SHOW_GRID_LAYOUT_KEY = "SHOW_GRID_LAYOUT_KEY";

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    private RecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;

    private DirectoryPresenter mDirectoryPresenter;

    private HorizontalScrollView mMainDirectoryScrollView;
    private Toolbar mainToolbar;
    private TextView mainToolbarTitleTextView;

    private DividerItemDecoration mDividerItemDecoration;

    private TextView mNumberOfDirectoriesTextView;
    private TextView mNumberOfFilesTextView;

    private boolean mShowGrid = false;

    private ListToGridBroadcastReceiver mListToGridBroadcastReceiver = new ListToGridBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                boolean changeToGrid = intent.getBooleanExtra(ListToGridBroadcastReceiver
                        .CHANGE_TO_GRID, false);

                boolean changeToList = intent.getBooleanExtra(ListToGridBroadcastReceiver
                        .CHANGE_TO_LIST, false);

                if (changeToGrid) {
                    changeListToGrid();
                }

                if (changeToList) {
                    changeGridToList();
                }
            }
        }
    };

    private NavigateBroadcastReceiver mNavigateBroadcastReceiver = new NavigateBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String navigateAction = intent.getStringExtra(NAVIGATE_ACTION);
                switch (navigateAction) {
                    case NAVIGATE_TO_EXTERNAL_STORAGE:
                        navigateToExternalStorage();
                        break;
                    case NAVIGATE_TO_INTERNAL_STORAGE:
                        navigateToInternalStorage(getContext());
                        break;
                }
            }
        }
    };

    @Override
    public void changeListToGrid() {
        mShowGrid = true;
        mDirectoryAdapter.setShowGrid(mShowGrid);
        mDirectoryRecyclerView.removeItemDecoration(mDividerItemDecoration);
        mDirectoryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUMBER_OF_COLUMNS_IN_GRID, StaggeredGridLayoutManager.VERTICAL));
        mDirectoryAdapter.notifyDataSetChanged();

    }

    @Override
    public void changeGridToList() {
        mShowGrid = false;
        mDirectoryAdapter.setShowGrid(mShowGrid);
        mDirectoryRecyclerView.addItemDecoration(mDividerItemDecoration, 0);
        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mDirectoryAdapter.notifyDataSetChanged();
    }

    public static DirectoryFragment getInstance(Bundle arguments) {
        DirectoryFragment directoryFragment = new DirectoryFragment();
        directoryFragment.setArguments(arguments);
        return directoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDirectoryPresenter = new DirectoryPresenter(this);
        mDividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration
                .VERTICAL);

        if (savedInstanceState != null) {
            mShowGrid = savedInstanceState.getBoolean(SHOW_GRID_LAYOUT_KEY,
                    false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainDirectoryScrollView = view.findViewById(R.id.mainDirectoryScrollView);
        mainToolbar = view.findViewById(R.id.mainToolbar);
        mainToolbarTitleTextView = mainToolbar.findViewById(R.id.mainToolbarTextView);
        mainToolbarTitleTextView.setTextColor(Color.WHITE);

        mNumberOfDirectoriesTextView = mainToolbar.findViewById(R.id.numberOfDirectoriesTextView);
        mNumberOfFilesTextView = mainToolbar.findViewById(R.id.numberOfFilesTextView);

        mDirectoryAdapter = new DirectoryAdapter(this);
        mDirectoryRecyclerView = view.findViewById(R.id.fileRecyclerView);
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setHasFixedSize(true);
        mDirectoryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mShowGrid) {
            mDirectoryRecyclerView.removeItemDecoration(mDividerItemDecoration);
            mDirectoryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUMBER_OF_COLUMNS_IN_GRID, StaggeredGridLayoutManager.VERTICAL));
        } else {
            mDirectoryRecyclerView.addItemDecoration(mDividerItemDecoration, 0);
            mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
        }

        if (savedInstanceState != null) {
            mDirectoryPresenter.onResumeState(savedInstanceState);
        }

        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission_group
                .STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            mDirectoryPresenter.onFilesRequest();
        } else {
            requestFilePermissions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver
                (mListToGridBroadcastReceiver, new IntentFilter(ListToGridBroadcastReceiver
                        .INTENT_FILTER_STRING));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver
                (mNavigateBroadcastReceiver, new IntentFilter(NavigateBroadcastReceiver
                        .INTENT_FILTER_STRING));

//        if (getActivity().getIntent().hasExtra(NavigateBroadcastReceiver.NAVIGATE_ACTION)) {
//            String navigateAction = getActivity().getIntent().getStringExtra(NavigateBroadcastReceiver.NAVIGATE_ACTION);
//
//            switch(navigateAction) {
//                case NavigateBroadcastReceiver.NAVIGATE_TO_EXTERNAL_STORAGE:
//                    navigateToExternalStorage();
//                    break;
//                case NavigateBroadcastReceiver.NAVIGATE_TO_INTERNAL_STORAGE:
//                    navigateToInternalStorage(getContext());
//                    break;
//            }
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mListToGridBroadcastReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mNavigateBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOW_GRID_LAYOUT_KEY, mShowGrid);
        mDirectoryPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void requestFilePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
                            .permission.WRITE_EXTERNAL_STORAGE} ,
                    PERMISSION_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mDirectoryPresenter.onFilesRequest();
                }
        }
    }

    @Override
    public boolean onBackPressed() {
        return mDirectoryPresenter.onBackPressed();
    }

    @Override
    public void setDirectoryTitle(String title) {
        if (mainToolbarTitleTextView != null) {
            mainToolbarTitleTextView.setText(title);
            mainToolbarTitleTextView.post(new Runnable() {
                @Override
                public void run() {
                    mMainDirectoryScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });
        }
    }

    @Override
    public void showNumberOfDirectories(int numberOfDirectories) {
        if (numberOfDirectories == 0) {
            mNumberOfDirectoriesTextView.setVisibility(View.GONE);
        } else {
            String numOfDirectoriesString = getString(R.string.folders) + ": " + String.valueOf
                    (numberOfDirectories);
            mNumberOfDirectoriesTextView.setText(numOfDirectoriesString);
            mNumberOfDirectoriesTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showNumberOfFiles(int numberOfFiles) {
        if (numberOfFiles == 0) {
            mNumberOfFilesTextView.setVisibility(View.GONE);
        } else {
            String numOfFilesString = getString(R.string.files) + ": " + String.valueOf(numberOfFiles);
            mNumberOfFilesTextView.setText(numOfFilesString);
            mNumberOfFilesTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showFiles(ArrayList<FileModel> fileModels) {
        mDirectoryAdapter.setFiles(fileModels);
    }

    @Override
    public void navigateToInternalStorage(Context context) {
        mDirectoryPresenter.onNavigateToInternalStorage(getContext());
    }

    @Override
    public void navigateToExternalStorage() {
        mDirectoryPresenter.onNavigateToExternalStorage();
    }

    @Override
    public void viewFile(FileModel fileModel) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setDataAndType(FileProvider.getUriForFile(getContext(),
                BuildConfig.APPLICATION_ID + ".provider", fileModel.getFile()),
                getMimeType(fileModel.getFile().getAbsolutePath()));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (myIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(myIntent);
        } else {
            Toast.makeText(getContext(), "No activity found to handle file type " + getMimeType(fileModel.getFile().getAbsolutePath()),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void showExtendedInformationOnFile(FileModel fileModel) {
        String fileSize = Formatter.formatFileSize(getContext(), fileModel.getFile().length());


        if (fileModel.getFile().isDirectory()) {

        } else {
            View view = LayoutInflater.from(getContext()).inflate(R.layout
                    .file_properties_layout, null);

            TextView fileNameTextView = view.findViewById(R.id.fileNameTextView);
            fileNameTextView.setText(fileModel.getFile().getName());

            TextView fileSizeTextView = view.findViewById(R.id.fileSizeTextView);
            fileSizeTextView.setText(fileSize);

            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.information)
                    .setView(view)
                    .setPositiveButton(R.string.ok, null)
                    .create();

            alertDialog.show();
        }
    }

    @Override
    public void onShortPress(FileModel fileModel) {
        mDirectoryPresenter.onShortFileModelPress(fileModel);
    }

    @Override
    public void onLongPress(FileModel fileModel) {
        mDirectoryPresenter.onLongFileModelPress(fileModel);
    }

    @Override
    public void directoryHasUpdated() {
        View mainView = getView();

        if (mainView != null) {
            mainView.post(new Runnable() {
                @Override
                public void run() {
                    mDirectoryPresenter.onFilesRequest();
                }
            });
        }
    }
}
