package org.starling.lghrunningapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.UriMatcher;

public class RoutePointsContentProvider extends ContentProvider {
    private static final int POINTS = 1;
    private SQLiteDatabase database;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String AUTHORITY = RoutePointsContentProvider.class.getCanonicalName();
    private static final String BASE_PATH = "points";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // https://c1ctech.com/android-content-provider-example-using-sqlite-database/

    public RoutePointsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case POINTS:
                return "vnd.android.cursor.dir/points";
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }   }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = database.insert(GpsDbHelper.TABLE_POINTS, null, contentValues);

        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(_uri, null);

            return _uri;
        }
        throw new SQLException("Insertion Failed for URI :" + uri);
    }

    @Override
    public boolean onCreate() {
        GpsDbHelper helper = new GpsDbHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case POINTS:
                cursor = database.query(GpsDbHelper.TABLE_POINTS, GpsDbHelper.ALL_COLUMNS,
                        selection, null, null, null, GpsDbHelper.TIME + " ASC");


                break;
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;  }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}