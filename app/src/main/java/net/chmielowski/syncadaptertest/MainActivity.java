package net.chmielowski.syncadaptertest;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import net.chmielowski.syncadaptertest.sync.SyncAdapter;

import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHORITY = "net.chmielowski.syncadaptertest";
    public static final String ACCOUNT_TYPE = "net.chmielowski.syncadaptertest";
    public static final String ACCOUNT = "Piotr Chmielowski";
    Account mAccount;
    private Switch isEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isEnabled = findViewById(R.id.enable_sync);

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        final Account[] accounts = manager.getAccountsByType(ACCOUNT_TYPE);
        Stream.of(accounts)
                .forEach(account -> Log.d("pchm", account.toString()));

        mAccount = accounts[0];

//        if (!manager.addAccountExplicitly(new Account(ACCOUNT, ACCOUNT_TYPE), null, null)) {
//            throw new RuntimeException();
//        }

        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING;
        ContentResolver.addStatusChangeListener(mask, which ->
                Log.d("pchm", "::onStatusChanged: " + which)
        );
    }

    public void sync(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(SyncAdapter.CALENDAR_ENABLED, isEnabled.isChecked());
        ContentResolver.requestSync(mAccount, AUTHORITY, bundle);
    }
}
