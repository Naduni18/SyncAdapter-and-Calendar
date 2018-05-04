package net.chmielowski.syncadaptertest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AuthenticatorService extends Service {

    private AccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        Log.d("pchm", getClass().getSimpleName() + "::onCreate");
        authenticator = new AccountAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        Log.d("pchm", getClass().getSimpleName() + "::onBind");
        return authenticator.getIBinder();
    }
}
