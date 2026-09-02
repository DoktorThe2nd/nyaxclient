package com.doktorthe2nd.nyax.types.stored;

import androidx.datastore.preferences.core.PreferencesKeys;

import java.util.Set;

public abstract class Stored<T> {
    private static final PrefStore<byte[]> BYTE_ARRAY_STORAGE = new PrefStore<>(PreferencesKeys::byteArrayKey);
    private static final PrefStore<Float> FLOAT_STORAGE = new PrefStore<>(PreferencesKeys::floatKey);
    private static final PrefStore<Integer> INT_STORAGE = new PrefStore<>(PreferencesKeys::intKey);
    private static final PrefStore<Boolean> BOOLEAN_STORAGE = new PrefStore<>(PreferencesKeys::booleanKey);
    private static final PrefStore<Double> DOUBLE_STORAGE = new PrefStore<>(PreferencesKeys::doubleKey);
    private static final PrefStore<Long> LONG_STORAGE = new PrefStore<>(PreferencesKeys::longKey);
    private static final PrefStore<String> STRING_STORAGE = new PrefStore<>(PreferencesKeys::stringKey);
    private static final PrefStore<Set<String>> STRING_SET_STORAGE = new PrefStore<>(PreferencesKeys::stringSetKey);

    public static Stored<Float> makeFloat(String identifier, Float default_value) {
        return makeT(identifier, default_value, FLOAT_STORAGE);
    }
    public static Stored<Integer> makeInteger(String identifier, Integer default_value) {
        return makeT(identifier, default_value, INT_STORAGE);
    }
    public static Stored<byte[]> makeByteArray(String identifier, byte[] default_value) {
        return makeT(identifier, default_value, BYTE_ARRAY_STORAGE);
    }
    public static Stored<Boolean> makeBoolean(String identifier, Boolean default_value) {
        return makeT(identifier, default_value, BOOLEAN_STORAGE);
    }
    public static Stored<Double> makeDouble(String identifier, Double default_value) {
        return makeT(identifier, default_value, DOUBLE_STORAGE);
    }
    public static Stored<Long> makeLong(String identifier, Long default_value) {
        return makeT(identifier, default_value, LONG_STORAGE);
    }
    public static Stored<String> makeString(String identifier, String default_value) {
        return makeT(identifier, default_value, STRING_STORAGE);
    }
    public static Stored<Set<String>> makeStringSet(String identifier, Set<String> default_value) {
        return makeT(identifier, default_value, STRING_SET_STORAGE);
    }

    private static <T> Stored<T> makeT(String identifier, T default_value, PrefStore<T> storage) {
        return new Stored<>(identifier, default_value) {
            @Override
            PrefStore<T> getStorage() {
                return storage;
            }
        };
    }

    private T object;
    private final String id;

    abstract PrefStore<T> getStorage();

    /**
     * Create variable and try to {@link #load()} its value. Default value will not be saved automatically.
     * If you need to save default value, use {@link #save()}
     * @param identifier unique(!) identifier for this variable
     * @param default_value value that will be set if {@link #load()} did nothing
     */
    public Stored(String identifier, T default_value) {
        object = default_value;
        id = identifier;
        load();
    }

    /**
     * Update value and save it
     * @param value new value
     * @return previous value
     */
    public T set(T value) {
        T old = object;
        object = value;
        save();
        return old;
    }

    /**
     * Get current value. This does not load value from storage. To load, see {@link #load()}
     * @return current value
     */
    public T get() {
        return object;
    }

    /**
     * Save current value to storage. Do nothing if current value is null
     */
    public void save() {
        if (object == null) return;
        getStorage().store(id, object);
    }

    /**
     * Load value from storage, save to current value. Do nothing if value not found or any other exception occurred
     */
    public void load() {
        var arr = getStorage().read(id);
        if (arr == null) return;
        object = arr;
    }
}
