package cj.com.fileexplorer.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cj.com.fileexplorer.views.DirectoryFragment;

/**
 * Navigation broadcast receiver broadcasts when a navigation item is selected in the navigation
 * drawer. Subscribed views can then decide on how they want to react to this change.
 *
 * @see DirectoryFragment for how this broadcast receiver is utilized.
 */
public abstract class NavigateBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_FILTER_STRING = "NAVIGATE_EVENTS";
    public static String NAVIGATE_ACTION = "NAVIGATE_ACTION";
    public static final String NAVIGATE_TO_EXTERNAL_STORAGE = "NAVIGATE_TO_EXTERNAL_STORAGE";
    public static final String NAVIGATE_TO_INTERNAL_STORAGE = "NAVIGATE_TO_INTERNAL_STORAGE";

    @Override
    public abstract void onReceive(Context context, Intent intent);
}
