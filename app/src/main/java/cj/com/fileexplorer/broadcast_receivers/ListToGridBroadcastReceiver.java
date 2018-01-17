package cj.com.fileexplorer.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by EpiphX on 1/14/18.
 */

public abstract class ListToGridBroadcastReceiver extends BroadcastReceiver {
    public static final String INTENT_FILTER_STRING = "LIST_TO_GRID_EVENTS";
    public static final String CHANGE_TO_GRID = "CHANGE_TO_GRID";
    public static final String CHANGE_TO_LIST = "CHANGE_TO_LIST";

    @Override
    public abstract void onReceive(Context context, Intent intent);
}
