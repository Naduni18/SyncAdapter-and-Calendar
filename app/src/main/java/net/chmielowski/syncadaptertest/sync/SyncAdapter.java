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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

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
    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        if (!extras.getBoolean(CALENDAR_ENABLED)) {
            deleteCalendarFor(account);
            return;
        }
        Cursor cursor = getCalendarsFor(account);
        if (!cursor.moveToFirst()) {
            insertCalendarFor(account);
        }
        cursor = getCalendarsFor(account);
        cursor.moveToFirst();
        final long id = cursor.getLong(0);
        Log.d("pchm", "will insert");
        final Uri inserted = insertEvent(id, account);
        Log.d("pchm", "inserted");
        showAllEvents(inserted);
        showAllEvents(id);
        Log.d("pchm", "events shown");
    }

    private void showAllEvents(final Uri inserted) {
        final Cursor cursor = resolver.query(
                inserted,
                new String[]{Events.TITLE},
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            Log.d("pchm", cursor.getString(0));
        }
    }

    @SuppressLint("MissingPermission")
    private Uri insertEvent(final long calendar, Account account) {
        final ContentValues values = new ContentValues();
        values.put(Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        values.put(Events.DURATION, 15 * 60 * 1000);
        values.put(Events.TITLE, "New event");
        values.put(Events.DESCRIPTION, "Group workout");
        values.put(Events.CALENDAR_ID, calendar);
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return resolver.insert(asSyncAdapter(Events.CONTENT_URI, account.name, account.type), values);
    }

    @SuppressLint("MissingPermission")
    private void showAllEvents(final long calendar) {
        final Cursor cursor = resolver.query(
                Events.CONTENT_URI,
                new String[]{Events.TITLE},
                Events.CALENDAR_ID + " = ?",
                new String[]{String.valueOf(calendar)},
                null
        );
        while (cursor.moveToNext()) {
            Log.d("pchm", cursor.getString(0));
        }
    }

    @SuppressLint("MissingPermission")
    private Cursor getCalendarsFor(final Account account) {
        return resolver.query(Calendars.CONTENT_URI, EVENT_PROJECTION, Calendars.ACCOUNT_TYPE + " = ?", new String[]{account.type}, null);
    }

    private void deleteCalendarFor(final Account account) {
        final int rows = resolver.delete(asSyncAdapter(Calendars.CONTENT_URI, account.name, account.type), Calendars.ACCOUNT_TYPE + " = ?", new String[]{account.type});
        if (rows > 1) {
            throw new AssertionError("rows > 1");
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

    private void insertCalendarFor(final Account account) {
        final ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, account.name);
        values.put(Calendars.ACCOUNT_TYPE, account.type);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "chmielowski.net");
        values.put(Calendars.CALENDAR_COLOR, Color.rgb(0xFF, 0x00, 0x00));
        final Uri insert = resolver.insert(asSyncAdapter(Calendars.CONTENT_URI, account.name, account.type), values);
        Log.d("pchm", getClass().getSimpleName() + "::insertCalendar: " + insert);
    }

    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
    }
}
