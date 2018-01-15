package cj.com.fileexplorer.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cj.com.fileexplorer.BuildConfig;
import cj.com.fileexplorer.R;
import cj.com.fileexplorer.adapters.DirectoryAdapter;
import cj.com.fileexplorer.presenters.DirectoryPresenter;
import cj.com.filemanager.models.FileModel;

import static cj.com.filemanager.FileUtils.getMimeType;

/**
 * Created by EpiphX on 1/14/18.
 */

public class DirectoryFragment extends Fragment implements DirectoryView, DirectoryAdapter.OnItemClickListener {
    // Request code for permissions from
    public static final int PERMISSION_EXTERNAL_STORAGE_REQUEST_CODE = 100;

    boolean mChangedToGrid = true;
    private RecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    private DirectoryPresenter mDirectoryPresenter;
    private Toolbar mainToolbar;

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
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDirectoryPresenter = new DirectoryPresenter(this);

        mainToolbar = view.findViewById(R.id.mainToolbar);

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
