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

import net.chmielowski.syncadaptertest.Dependency;

import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String CALENDAR_ENABLED = "calendar enabled";
    private final ContentResolver resolver;
    private final Dependency dependency;

    @Inject
    SyncAdapter(final Context context, final Dependency dependency) {
        super(context, true);
        this.dependency = dependency;
        this.resolver = context.getContentResolver();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        Log.d("pchm", getClass().getSimpleName() + "::onPerformSync: " + dependency);
        if (!extras.getBoolean(CALENDAR_ENABLED)) {
            deleteCalendarFor(account);
            return;
        }
        try (final Cursor cursor = getCalendars(account)) {
            final String calendarId;
            if (cursor.moveToFirst()) {
                calendarId = cursor.getString(0);
            } else {
                calendarId = insertCalendarFor(account);
            }
            insertEvent(calendarId, account);
        }
    }

    @SuppressLint("MissingPermission")
    private void insertEvent(final String calendar, Account account) {
        final ContentValues values = new ContentValues();
        values.put(Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        values.put(Events.DURATION, 15 * 60 * 1000);
        values.put(Events.TITLE, "New event");
        values.put(Events.DESCRIPTION, "Group workout");
        values.put(Events.CALENDAR_ID, calendar);
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        resolver.insert(asSyncAdapter(Events.CONTENT_URI, account.name, account.type), values);
    }

    @SuppressWarnings("unused")
    private void showAllEvents(final long calendar) {
        try (final Cursor cursor = getEvents(calendar)) {
            while (cursor.moveToNext()) {
                Log.d("pchm", cursor.getString(0));
            }
        }
    }

    @SuppressLint("MissingPermission")
    private Cursor getEvents(final long calendar) {
        return resolver.query(
                Events.CONTENT_URI,
                new String[]{Events.TITLE},
                Events.CALENDAR_ID + " = ?",
                new String[]{String.valueOf(calendar)},
                null
        );
    }

    @SuppressLint("MissingPermission")
    private Cursor getCalendars(final Account account) {
        return resolver.query(
                Calendars.CONTENT_URI,
                new String[]{
                        Calendars._ID,
                        Calendars.ACCOUNT_NAME,
                        Calendars.CALENDAR_DISPLAY_NAME,
                        Calendars.OWNER_ACCOUNT,
                        Calendars.ACCOUNT_TYPE
                },
                Calendars.ACCOUNT_TYPE + " = ?",
                new String[]{account.type},
                null
        );
    }

    private void deleteCalendarFor(final Account account) {
        final int rows = resolver.delete(
                asSyncAdapter(Calendars.CONTENT_URI, account.name, account.type),
                Calendars.ACCOUNT_TYPE + " = ?",
                new String[]{account.type}
        );
        if (rows > 1) {
            throw new AssertionError("rows > 1");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String insertCalendarFor(final Account account) {
        final ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, account.name);
        values.put(Calendars.ACCOUNT_TYPE, account.type);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "chmielowski.net");
        values.put(Calendars.CALENDAR_COLOR, Color.rgb(0xFF, 0x00, 0x00));
        final Uri insert = resolver.insert(asSyncAdapter(Calendars.CONTENT_URI, account.name, account.type), values);
        return insert.getLastPathSegment();
    }

    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
    }
}
