package cj.com.fileexplorer;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import cj.com.fileexplorer.broadcast_receivers.ListToGridBroadcastReceiver;
import cj.com.fileexplorer.broadcast_receivers.NavigateBroadcastReceiver;
import cj.com.fileexplorer.views.BaseFragment;
import cj.com.fileexplorer.views.CreditsActivity;

public class MainActivity extends AppCompatActivity {
    public static final String CHANGED_TO_GRID_KEY = "CHANGED_TO_GRID_KEY";
    private static final String TAG = "MainActivity-";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    // Keeps track if changed to grid was selected.
    boolean mChangedToGrid = false;
    private int mSelectedNavigationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // external storage shortcuts.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            Intent[] intents = new Intent[2];


            Intent launchActivityIntent = new Intent(MainActivity.this, MainActivity.class);
            launchActivityIntent.setAction(Intent.ACTION_VIEW);

            launchActivityIntent.putExtra("NAVIGATION_SHORTCUT_KEY", "EXTERNAL_STORAGE");

            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "id1")
                    .setShortLabel("External Storage")
                    .setLongLabel("Open external storage")
                    .setIcon(Icon.createWithResource(getBaseContext(), R.drawable.ic_folder_black_24dp))
                    .setIntent(launchActivityIntent)
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
        }



        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation);
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

        if (savedInstanceState != null) {
            mChangedToGrid = savedInstanceState.getBoolean(CHANGED_TO_GRID_KEY, true);
        }

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_external_storage:
                        // Post navigation to external storage.
                        navigateToExternalStorage();
                        break;
                    case R.id.navigation_internal_storage:
                        // Post navigation to internal storage.
                        navigateToInternalStorage();
                        break;
                }

                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void navigateToInternalStorage() {
        mSelectedNavigationItem = R.id.navigation_internal_storage;
        Intent navigateToInternalIntent = new Intent(NavigateBroadcastReceiver
                .INTENT_FILTER_STRING);
        navigateToInternalIntent.putExtra(NavigateBroadcastReceiver
                .NAVIGATE_ACTION, NavigateBroadcastReceiver
                .NAVIGATE_TO_INTERNAL_STORAGE);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(navigateToInternalIntent);
    }

    private void navigateToExternalStorage() {
        mSelectedNavigationItem = R.id.navigation_external_storage;
        Intent navigateToExternalIntent = new Intent(NavigateBroadcastReceiver
                .INTENT_FILTER_STRING);
        navigateToExternalIntent.putExtra(NavigateBroadcastReceiver
                        .NAVIGATE_ACTION, NavigateBroadcastReceiver.NAVIGATE_TO_EXTERNAL_STORAGE);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(navigateToExternalIntent);
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CHANGED_TO_GRID_KEY, mChangedToGrid);
        super.onSaveInstanceState(outState);
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
                if (!mChangedToGrid) {
                    item.setTitle("List");
                    item.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                            .ic_view_list_black_24dp));
                    // Broadcast event that list should be shown in a grid format.
                    Intent intent = new Intent(ListToGridBroadcastReceiver.INTENT_FILTER_STRING);
                    intent.putExtra(ListToGridBroadcastReceiver.CHANGE_TO_GRID, true);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                    mChangedToGrid = true;
                } else {
                    item.setTitle("Grid");
                    item.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                            .ic_view_module_black_24dp));
                    // Broadcast event that list should be shown in a list format.
                    Intent intent = new Intent(ListToGridBroadcastReceiver.INTENT_FILTER_STRING);
                    intent.putExtra(ListToGridBroadcastReceiver.CHANGE_TO_LIST, true);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                    mChangedToGrid = false;
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

        final MenuItem listMenuItem;

        if (!mChangedToGrid) {
            listMenuItem = menu.add(0, 0, 0, "Grid");
            listMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                    .ic_view_module_black_24dp));
        } else {
            listMenuItem = menu.add(0, 0, 0, "List");
            listMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable
                    .ic_view_list_black_24dp));
        }

        listMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 0, "Credits");

        return true;
    }

    @Override
    public void onBackPressed() {
        // If the directory presenter does not return TRUE (implying that it will be handling the
        // on back pressed), then call the activities on back pressed to manage the activity stack.
        List<Fragment> baseFragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : baseFragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof BaseFragment) {
                if (!((BaseFragment) fragment).onBackPressed()) {
                    super.onBackPressed();
                }
            }
        }
    }
}
