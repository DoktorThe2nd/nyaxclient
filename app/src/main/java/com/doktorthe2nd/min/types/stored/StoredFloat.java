package com.doktorthe2nd.min.types.stored;

public class StoredFloat {
    private float object;
    private final String id;

    /** More valid way to store float. <p> See {@link Stored#Stored(String, Object)} */
    public StoredFloat(String identifier, float default_value) {
        object = default_value;
        id = identifier;
        load();
    }

    /**
     * Update value and save it
     * @param value new value
     * @return previous value
     */
    public float set(float value) {
        float old = object;
        object = value;
        save();
        return old;
    }

    /**
     * Get current value. This does not load value from storage. To load, see {@link #load()}
     * @return current value
     */
    public float get() {
        return object;
    }

    /**
     * Save current value to storage. Do nothing if current value is null
     */
    public void save() {
        PrefStore.storeFloat(id, object);
    }

    /**
     * Load value from storage, save to current value. Do nothing if value not found or any other exception occurred
     */
    public void load() {
        Float value = PrefStore.readFloat(id);
        if (value == null) return;
        object = value;
    }
}
