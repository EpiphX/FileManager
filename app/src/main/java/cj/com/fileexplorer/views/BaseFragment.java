package cj.com.fileexplorer.views;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
    /**
     * Fragments that extend the base fragment can override this method if they want to handle
     * their own on back pressed behavior.
     *
     * @return      TRUE will stop the parent activity from handling its own on back pressed
     *              functionality.
     *
     *              FALSE will allow the parent activity to handle its own on back pressed
     *              functionality.
     */
    public boolean onBackPressed() {
        // Default behavior will let the activity handles its own on back pressed functionality.
        return false;
    }
}
