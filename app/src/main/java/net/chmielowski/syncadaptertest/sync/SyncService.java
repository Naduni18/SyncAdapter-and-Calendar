package net.chmielowski.syncadaptertest.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import net.chmielowski.syncadaptertest.DaggerMainComponent;

import javax.inject.Inject;

public class SyncService extends Service {
    @Inject
    SyncAdapter adapter;

    @Override
    public void onCreate() {
        DaggerMainComponent.builder()
                .bindContext(this)
                .build()
                .inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("pchm", getClass().getSimpleName() + "::onBind");
        return adapter.getSyncAdapterBinder();
    }
}
