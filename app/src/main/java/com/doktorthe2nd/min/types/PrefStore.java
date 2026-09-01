package com.doktorthe2nd.min.types;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import com.doktorthe2nd.min.MainActivity;

import io.reactivex.Single;

class PrefStore {
    private static RxDataStore<Preferences> STORE = null;
    private static RxDataStore<Preferences> getStore() {
        if (STORE == null) {
            STORE = new RxPreferenceDataStoreBuilder(MainActivity.appContext, "pref_store").build();
        }
        return STORE;
    }

    public static void storeByteArray(String key, byte[] value) {
        Preferences.Key<byte[]> prefKey = PreferencesKeys.byteArrayKey(key);

        getStore().updateDataAsync(prefsIn -> {
            MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
            mutablePrefs.set(prefKey, value);
            return Single.just(mutablePrefs);
        });
    }

    public static byte[] readByteArray(String key) {
        Preferences.Key<byte[]> prefKey = PreferencesKeys.byteArrayKey(key);

        Single<byte[]> value = getStore().data()
                .firstOrError()
                .map(prefs -> prefs.get(prefKey));

        try {
            return value.blockingGet();
        } catch (Exception e) {
            return null;
        }
    }
}
