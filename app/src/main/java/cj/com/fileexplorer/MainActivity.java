package cj.com.fileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.MimeTypeFilter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
import java.util.List;

import cj.com.fileexplorer.adapters.DirectoryAdapter;
import cj.com.fileexplorer.broadcast_receivers.ListToGridBroadcastReceiver;
import cj.com.fileexplorer.presenters.DirectoryPresenter;
import cj.com.fileexplorer.views.BaseFragment;
import cj.com.fileexplorer.views.CreditsActivity;
import cj.com.fileexplorer.views.DirectoryView;
import cj.com.filemanager.models.FileModel;

import static cj.com.filemanager.FileUtils.getMimeType;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity-";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    // Keeps track if changed to grid was selected.
    boolean mChangedToGrid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // external storage shortcuts.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(R.string.app_name);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case 0:
                if (mChangedToGrid) {
                    item.setTitle("List");
                    item.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                            .ic_view_list_black_24dp));
                    // Broadcast event that list should be shown in a grid format.
                    Intent intent = new Intent(ListToGridBroadcastReceiver.INTENT_FILTER_STRING);
                    intent.putExtra(ListToGridBroadcastReceiver.CHANGE_TO_GRID, true);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                    mChangedToGrid = false;
                } else {
                    item.setTitle("Grid");
                    item.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                            .ic_view_module_black_24dp));
                    // Broadcast event that list should be shown in a list format.
                    Intent intent = new Intent(ListToGridBroadcastReceiver.INTENT_FILTER_STRING);
                    intent.putExtra(ListToGridBroadcastReceiver.CHANGE_TO_LIST, true);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                    mChangedToGrid = true;
                }
                break;
            case 1:
                Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
                startActivity(intent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem listMenuItem = menu.add(0,0,0,"Grid");
        listMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                .ic_view_module_black_24dp));
        listMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0,1,0,"Credits");

        return true;
    }

    @Override
    public void onBackPressed() {
        // If the directory presenter does not return TRUE (implying that it will be handling the
        // on back pressed), then call the activities on back pressed to manage the activity stack.
        List<Fragment> baseFragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : baseFragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof BaseFragment){
                if (!((BaseFragment) fragment).onBackPressed()) {
                    super.onBackPressed();
                }
            }
        }
    }
}
