package cj.com.fileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.MimeTypeFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.util.ArrayList;

import cj.com.fileexplorer.adapters.DirectoryAdapter;
import cj.com.fileexplorer.presenters.DirectoryPresenter;
import cj.com.fileexplorer.views.CreditsActivity;
import cj.com.fileexplorer.views.DirectoryView;
import cj.com.filemanager.models.FileModel;

import static cj.com.filemanager.FileUtils.getMimeType;

public class MainActivity extends AppCompatActivity implements DirectoryView, DirectoryAdapter.OnItemClickListener {
    private static final String TAG = "MainActivity-";
    public static final int PERMISSION_EXTERNAL_STORAGE_REQUEST_CODE = 100;

    boolean mChangedToGrid = true;
    private RecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    private DirectoryPresenter mDirectoryPresenter;
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDirectoryPresenter = new DirectoryPresenter(this);

        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        mDirectoryAdapter = new DirectoryAdapter(this);
        mDirectoryRecyclerView = findViewById(R.id.fileRecyclerView);
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setHasFixedSize(true);
        mDirectoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(),
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
    public boolean onCreateOptionsMenu(Menu menu) {

        final MenuItem listMenuItem = menu.add("Grid");
        listMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                .ic_view_module_black_24dp));
        listMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        listMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mChangedToGrid) {
                    listMenuItem.setTitle("List");
                    listMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                            .ic_view_list_black_24dp));
                    mDirectoryRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext
                            (), NUMBER_OF_COLUMNS_IN_GRID));
                    mDirectoryAdapter.notifyDataSetChanged();

                    // Broadcast event that list should be shown in a grid format.
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent());
                    mChangedToGrid = false;
                } else {
                    listMenuItem.setTitle("Grid");
                    listMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                            .ic_view_module_black_24dp));
                    mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                            LinearLayoutManager.VERTICAL, false));
                    mDirectoryAdapter.notifyDataSetChanged();

                    // Broadcast event that list should be shown in a list format.
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent());
                    mChangedToGrid = true;
                }

                return true;
            }
        });

        final MenuItem creditsMenuItem = menu.add("Credits");
        creditsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        creditsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
                startActivity(intent);
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mDirectoryPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // If the directory presenter does not return TRUE (implying that it will be handling the
        // on back pressed), then call the activities on back pressed to manage the activity stack.
        if (!mDirectoryPresenter.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save off last visited directory into shared preferences, so that it can be resumed at
        // a later point.
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
    public void showExtendedInformationOnFile(FileModel fileModel) {
        Uri fileUri = FileProvider.getUriForFile(getBaseContext(),
                BuildConfig.APPLICATION_ID + ".provider", fileModel.getFile());

        Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);


                Toast.makeText(this, String.valueOf(fileModel.getFile().length()),
                        Toast.LENGTH_SHORT)
                        .show();
            }

            cursor.close();
        }
    }

    @Override
    public void clearFiles() {
        mDirectoryAdapter.clearFiles();
    }

    @Override
    public void viewFile(FileModel fileModel) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setDataAndType(FileProvider.getUriForFile(getBaseContext(),
                BuildConfig.APPLICATION_ID + ".provider", fileModel.getFile()),
                getMimeType(fileModel.getFile().getAbsolutePath()));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(myIntent);
    }

    @Override
    public void navigateToInternalStorage() {

    }

    @Override
    public void navigateToExternalStorage() {

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
