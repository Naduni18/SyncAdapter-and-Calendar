package net.chmielowski.syncadaptertest;

import android.content.Context;

import net.chmielowski.syncadaptertest.sync.SyncService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component
public interface MainComponent {
    void inject(SyncService service);

    @Component.Builder
    interface Builder {
        MainComponent build();

        @BindsInstance
        Builder bindContext(Context context);
    }
}
