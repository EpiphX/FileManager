package cj.com.fileexplorer.views;

import android.Manifest;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Toolbar mainToolbar;
    private TextView mainToolbarTitleTextView;

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
        mDirectoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS_IN_GRID));
        mDirectoryAdapter.notifyDataSetChanged();
        mShowGrid = true;
    }

    @Override
    public void changeGridToList() {
        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mDirectoryAdapter.notifyDataSetChanged();
        mShowGrid = false;
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
        mainToolbar = view.findViewById(R.id.mainToolbar);
        mainToolbarTitleTextView = mainToolbar.findViewById(R.id.mainToolbarTextView);
        mainToolbarTitleTextView.setTextColor(Color.WHITE);

        mDirectoryAdapter = new DirectoryAdapter(this);
        mDirectoryRecyclerView = view.findViewById(R.id.fileRecyclerView);
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setHasFixedSize(true);
        mDirectoryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mShowGrid) {
            mDirectoryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUMBER_OF_COLUMNS_IN_GRID, StaggeredGridLayoutManager.VERTICAL));
        } else {
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
        }
    }

    @Override
    public void showFiles(ArrayList<FileModel> fileModels) {
        mDirectoryAdapter.setFiles(fileModels);
    }

    @Override
    public void clearFiles() {

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
        // TODO: Look into popping up a dialog with extended information on the selected file.
    }

    @Override
    public void onShortPress(FileModel fileModel) {
        mDirectoryPresenter.onShortFileModelPress(fileModel);
    }

    @Override
    public void onLongPress(FileModel fileModel) {
        mDirectoryPresenter.onLongFileModelPress(fileModel);
    }
}
