package com.doktorthe2nd.min.types;

public class Stored<T> {
    private T object;
    private final String id;

    /**
     * Create Stored&lt;T&gt; and try to {@link #load()} its value. Default value will not be saved automatically.
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
        PrefStore.storeByteArray(id, (byte[])object);
    }

    /**
     * Load value from storage, save to current value. Do nothing if value not found or any other exception occurred
     */
    public void load() {
        byte[] arr = PrefStore.readByteArray(id);
        if (arr == null) return;
        object = (T)arr;
    }
}
