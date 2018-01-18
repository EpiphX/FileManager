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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cj.com.fileexplorer.BuildConfig;
import cj.com.fileexplorer.R;
import cj.com.fileexplorer.adapters.DirectoryAdapter;
import cj.com.fileexplorer.broadcast_receivers.ListToGridBroadcastReceiver;
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

    boolean mChangedToGrid = true;
    private RecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    private DirectoryPresenter mDirectoryPresenter;
    private Toolbar mainToolbar;

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

    @Override
    public void changeListToGrid() {
        mDirectoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS_IN_GRID));
        mDirectoryRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDirectoryAdapter.notifyDataSetChanged();
            }
        }, 30);
    }

    @Override
    public void changeGridToList() {
        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mDirectoryRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDirectoryAdapter.notifyDataSetChanged();
            }
        }, 30);
    }

    public static DirectoryFragment getInstance(Bundle arguments) {
        DirectoryFragment directoryFragment = new DirectoryFragment();
        directoryFragment.setArguments(arguments);
        return directoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDirectoryPresenter = new DirectoryPresenter(this);

        mainToolbar = view.findViewById(R.id.mainToolbar);
        mainToolbar.setTitleTextColor(Color.WHITE);

        mDirectoryAdapter = new DirectoryAdapter(this);
        mDirectoryRecyclerView = view.findViewById(R.id.fileRecyclerView);
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setHasFixedSize(true);
        mDirectoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState != null) {
            mDirectoryPresenter.onResumeState(savedInstanceState);
        }

        requestFilePermissions();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver
                (mListToGridBroadcastReceiver, new IntentFilter(ListToGridBroadcastReceiver
                        .INTENT_FILTER_STRING));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mListToGridBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        mainToolbar.setTitle(title);
    }

    @Override
    public void showFiles(ArrayList<FileModel> fileModels) {
        mDirectoryAdapter.setFiles(fileModels);
    }

    @Override
    public void clearFiles() {

    }

    @Override
    public void navigateToInternalStorage() {

    }

    @Override
    public void navigateToExternalStorage() {

    }

    @Override
    public void viewFile(FileModel fileModel) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setDataAndType(FileProvider.getUriForFile(getContext(),
                BuildConfig.APPLICATION_ID + ".provider", fileModel.getFile()),
                getMimeType(fileModel.getFile().getAbsolutePath()));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(myIntent);
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
