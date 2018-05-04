package net.chmielowski.syncadaptertest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class StubProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        Log.d("pchm", getClass().getSimpleName() + "::onCreate");
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.d("pchm", getClass().getSimpleName() + "::getType");
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d("pchm", getClass().getSimpleName() + "::query");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("pchm", getClass().getSimpleName() + "::insert");
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d("pchm", getClass().getSimpleName() + "::delete");
        return 0;
    }

    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        Log.d("pchm", getClass().getSimpleName() + "::update");
        return 0;
    }
}
