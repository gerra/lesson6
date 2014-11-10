package ru.android.german.lesson7;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by german on 08.11.14.
 */
public class RSSResultReceiver extends ResultReceiver {
    Context context;
    public RSSResultReceiver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult (int resultCode, Bundle resultData) {
        // Code to process resultData here
        Cursor cursor = context.getContentResolver().query(FeedContentProvider.FEEDS_CONTENT_URI,
                null, null, null, "title");
        ((MainActivity)context).adapter.swapCursor(cursor);
    }
}
