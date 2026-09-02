package com.doktorthe2nd.nyax.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapContainer {
    private Map<Object, Object> map;

    @SuppressWarnings("unchecked")
    public MapContainer(Object map) {
        this(map instanceof Map<?,?> ? (Map<Object, Object>)map : null);
    }
    public MapContainer(Map<Object, Object> map) {
        if (map != null) this.map = map;
        else this.map = new HashMap<>();
    }
    public MapContainer() {
        this.map = new HashMap<>();
    }

    public static MapContainer of(Map<Object, Object> map) {
        return new MapContainer(map);
    }
    public static MapContainer of(Object map) {
        return new MapContainer(map);
    }

    public void setMap(Map<Object, Object> map) {
        this.map = map;
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object put(Object key, Object value) {
        return map.put(key, value);
    }
    public MapContainer putc(Object key, Object value) {
        map.put(key, value);
        return this;
    }
    public Object remove(Object key) {
        return map.remove(key);
    }
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    public Object get(Object key) {
        return map.get(key);
    }
    public String getString(Object key) {
        return (String)get(key);
    }
    public Integer getInt(Object key) {
        return (Integer)get(key);
    }
    public Long getLong(Object key) {
        return (Long)get(key);
    }

    public Object getOr(Object key, Object fallback) {
        Object ret = map.get(key);
        if (ret == null) return fallback;
        return ret;
    }
    public String getStringOr(Object key, String fallback) {
        return (String)getOr(key, fallback);
    }
    public int getIntOr(Object key, int fallback) {
        return (int)getOr(key, fallback);
    }
    public long getLongOr(Object key, long fallback) {
        return (long)getOr(key, fallback);
    }

    /**
     * Get Object:Object map by key
     * @param key self-explanatory
     * @return map if got one, null if not
     */
    public Map<Object, Object> getMap(Object key) {
        if (get(key) instanceof Map<?,?>) return (Map<Object, Object>)get(key);
        return null;
    }
    /**
     * Get list of Object by key
     * @param key self-explanatory
     * @return list if got one, null if not
     */
    public ArrayList<Object> getList(Object key) {
        if (get(key) instanceof ArrayList<?>) return (ArrayList<Object>)get(key);
        return null;
    }
    /**
     * Get list of Object:Object maps by key
     * @param key self-explanatory
     * @return list of maps if got one, null if not
     */
    public ArrayList<Map<Object, Object>> getMapsArray(Object key) {
        if (!(get(key) instanceof ArrayList<?>)) return null;
        ArrayList<?> maps = (ArrayList<?>)get(key);
        if (maps.isEmpty()) return new ArrayList<>();
        if (!(maps.get(0) instanceof Map<?,?>)) return null;
        return (ArrayList<Map<Object, Object>>)maps;
    }
    /**
     * Same as {@link #getMap(Object)}, but wraps it with MapContainer
     * @param key self-explanatory
     * @return MapContainer, empty if not found
     */
    public MapContainer getc(Object key) {
        return new MapContainer(getMap(key));
    }
}
