package net.chmielowski.syncadaptertest.sync;

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
import android.provider.CalendarContract.Calendars;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String CALENDAR_ENABLED = "calendar enabled";
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
            Calendars._ID,
            Calendars.ACCOUNT_NAME,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.OWNER_ACCOUNT,
            Calendars.ACCOUNT_TYPE
    };


    @SuppressWarnings("ConstantConditions")
    @SuppressLint({"MissingPermission", "Recycle"})
    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        final boolean enabled = extras.getBoolean(CALENDAR_ENABLED);
        Log.d("pchm", getClass().getSimpleName() + "::onPerformSync " + enabled);
        final Uri uri = Calendars.CONTENT_URI;

        try {
//            insertCalendar(account, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("pchm", getClass().getSimpleName() + "::inserted");

//        showAllCalendars(uri);

        Log.d("pchm", " ---- My calendars ---- ");
        final String[] args = {account.type};
        delete(account, uri);
        final Cursor myCalendars = resolver.query(uri, EVENT_PROJECTION, Calendars.ACCOUNT_TYPE + " = ?", args, null);
        while (myCalendars.moveToNext()) {
            long calID = myCalendars.getLong(0);
            String displayName = myCalendars.getString(2);
            String accountName = myCalendars.getString(1);
            String accountType = myCalendars.getString(4);
            Log.d("pchm", String.format("#%d: %s, %s (%s)", calID, displayName, accountName, accountType));
        }

    }

    private void delete(final Account account, final Uri uri) {
        final int rows = resolver.delete(asSyncAdapter(uri, account.name, account.type), Calendars.ACCOUNT_TYPE + " = ?", new String[]{account.type});
        if (rows != 1) {
            throw new AssertionError("rows != 1");
        }
    }

    private void showAllCalendars(final Uri uri) {
        final Cursor calendars = resolver.query(uri, EVENT_PROJECTION, null, null, null);
        while (calendars.moveToNext()) {
            long calID = calendars.getLong(0);
            String displayName = calendars.getString(2);
            String accountName = calendars.getString(1);
            String accountType = calendars.getString(4);
            Log.d("pchm", String.format("#%d: %s, %s (%s)", calID, displayName, accountName, accountType));
        }
    }

    private void insertCalendar(final Account account, final Uri uri) {
        final ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, account.name);
        values.put(Calendars.ACCOUNT_TYPE, account.type);
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
