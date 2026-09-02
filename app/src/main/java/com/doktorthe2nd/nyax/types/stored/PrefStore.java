package com.doktorthe2nd.nyax.types.stored;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import com.doktorthe2nd.nyax.MainActivity;

import io.reactivex.Single;

class PrefStore<T> {
    private static RxDataStore<Preferences> STORE = null;
    private static RxDataStore<Preferences> getStore() {
        if (STORE == null) {
            STORE = new RxPreferenceDataStoreBuilder(MainActivity.appContext, "pref_store").build();
        }
        return STORE;
    }

    @FunctionalInterface
    public interface KeyGen<T> {
        Preferences.Key<T> get(String name);
    }

    private final KeyGen<T> keyGen;

    public PrefStore(KeyGen<T> keyGen) {
        this.keyGen = keyGen;
    }

    public void store(String key, T value) {
        Preferences.Key<T> prefKey = keyGen.get(key);

        getStore().updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.set(prefKey, value);
            return Single.just(mutablePrefs);
        });
    }

    public T read(String key) {
        Preferences.Key<T> prefKey = keyGen.get(key);

        Single<T> value = getStore().data()
                .firstOrError()
                .map(prefs -> prefs.get(prefKey));

        try {
            return value.blockingGet();
        } catch (Exception e) {
            return null;
        }
    }
}
