package net.chmielowski.syncadaptertest.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
    private static SyncAdapter adapter = null;
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        Log.d("pchm", getClass().getSimpleName() + "::onCreate");
        synchronized (lock) {
            if (adapter == null) {
                adapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("pchm", getClass().getSimpleName() + "::onBind");
        return adapter.getSyncAdapterBinder();
    }
}
