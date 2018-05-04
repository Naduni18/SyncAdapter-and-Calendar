package net.chmielowski.syncadaptertest;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

import static android.provider.CalendarContract.ACCOUNT_TYPE_LOCAL;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final ContentResolver resolver;

    public SyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
    }

    public SyncAdapter(final Context context, final boolean autoInitialize,
                       final boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }


    private static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    @SuppressWarnings("ConstantConditions")
    @SuppressLint({"MissingPermission", "Recycle"})
    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        Log.d("pchm", getClass().getSimpleName() + "::onPerformSync");
        Cursor cur;
        Uri uri = Calendars.CONTENT_URI;
        cur = resolver.query(uri, EVENT_PROJECTION, null, null, null);

        try {
            insertCalendar(account, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("pchm", getClass().getSimpleName() + "::inserted");

        while (cur.moveToNext()) {
            long calID = cur.getLong(PROJECTION_ID_INDEX);
            String displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            String accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            String ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            Log.d("pchm", String.format("#%d: %s, %s (%s)", calID, displayName, accountName, ownerName));
        }
    }

    private void insertCalendar(final Account account, final Uri uri) {
        final ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, account.name);
        values.put(Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE_LOCAL);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "Nowy kalendarz");
        final Uri insert = resolver.insert(asSyncAdapter(uri, account.name, account.type), values);
        Log.d("pchm", getClass().getSimpleName() + "::insertCalendar: " + insert);
    }

    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
    }
}
