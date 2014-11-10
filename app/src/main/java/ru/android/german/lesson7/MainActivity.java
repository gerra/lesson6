package ru.android.german.lesson7;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by german on 20.10.14.
 */
public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FeedContentProvider.FEED_ID,
                FeedContentProvider.FEED_TITLE,
                FeedContentProvider.FEED_LINK
        };
        return new CursorLoader(this, FeedContentProvider.FEEDS_CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    ListView listView;
    SimpleCursorAdapter adapter;
    RSSResultReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);
        receiver = new RSSResultReceiver(new Handler(), this);
        int layoutID = android.R.layout.simple_list_item_2;
        String from[] = {
                FeedContentProvider.FEED_TITLE,
                FeedContentProvider.FEED_LINK
        };
        int to[] = {
                android.R.id.text1,
                android.R.id.text2
        };
        adapter = new SimpleCursorAdapter(this, layoutID, null, from, to, 0);

        /*adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                                if (columnIndex == 1) {
                                        ((TextView) view).setText(cursor.getString(1));
                                        return true;
                                    }
                                return false;
                            }
                    });*/

        listView = (ListView)this.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(i);
                String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));
                Intent intent = new Intent(getBaseContext(), WebActivity.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Feed clickedFeed = feeds.get(i);
                Feed clickedFeed = FeedContentProvider.feeds.get(i);
                Intent intent = new Intent(getBaseContext(), WebActivity.class);
                intent.putExtra("link", clickedFeed.getLink());
                startActivity(intent);
            }
        });*/
       getLoaderManager().initLoader(0, null, this);
    }

    private void clearFeeds() {
        getContentResolver().delete(FeedContentProvider.FEEDS_CONTENT_URI, null, null);
//        receiver.send(0, null);
    }

    private void refreshFeeds() {
        // clearFeeds without displaying on listView
        getContentResolver().delete(FeedContentProvider.FEEDS_CONTENT_URI, null, null);
        // refreshing start
        Intent intent = new Intent(this, FeedLoader.class);
        intent.putExtra("url", "http://echo.msk.ru/interview/rss-fulltext.xml");
        intent.putExtra("receiver", receiver);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshFeeds();
                return true;
            case R.id.action_clear:
                clearFeeds();
                return true;
            case R.id.action_get:
                Cursor c = getContentResolver().query(FeedContentProvider.FEEDS_CONTENT_URI, null, null, null, "title");
                System.out.println(c.getCount());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
