package com.doktorthe2nd.nyax.modules;

import com.doktorthe2nd.nyax.types.MapContainer;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    public static Profile myProfile = new Profile();

    private final MapContainer contact = new MapContainer();
    //private List<Object> options = new ArrayList<>();

    public void setData(Map<Object, Object> data) {
        MapContainer profile = new MapContainer(data);
        contact.setMap(profile.getMap("contact"));
        //options = profile.getArray("profileOptions");
    }

    public long getId() {
        return contact.getLongOr("id", 0);
    }

    public String getDescription() {
        return contact.getString("description");
    }
    public long getPhotoId() {
        return contact.getLongOr("photoId", 0);
    }
    public String getNormalPhone() {
        String phone = contact.getString("phone");
        if (phone == null) return "unknown";
        return "+" + phone;
    }
    public String getPhone() {
        String phone = getNormalPhone();
        if (phone.length() != 12) return phone;
        return phone.substring(0, 2) +
                "(" +
                phone.substring(2, 5) +
                ")" +
                phone.substring(5, 8) +
                "-" +
                phone.substring(8, 10) +
                "-" +
                phone.substring(10);
    }

    public Map<Object, Object> getNames() {
        var names = contact.getMapsArray("names");
        if (names == null || names.isEmpty()) {
            var ret = new HashMap<>();
            ret.put("firstName", "<profile.contact.names is null>");
            return ret;
        }
        return names.get(0);
    }
    public String getFirstName() {
        var names = getNames();
        if (names == null) return "<profile.contact.names[0] is null>";
        if (names.get("firstName") == null) return "";
        else return names.get("firstName").toString();
    }
    public String getSecondName() {
        var names = getNames();
        if (names == null) return "<profile.contact.names[0] is null>";
        if (names.get("secondName") == null) return "";
        else return names.get("secondName").toString();
    }
    public String getFullName() {
        return getFirstName() + " " + getSecondName();
    }
}
