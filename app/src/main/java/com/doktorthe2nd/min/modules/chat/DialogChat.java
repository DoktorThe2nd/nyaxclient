package com.doktorthe2nd.min.modules.chat;

import com.doktorthe2nd.min.types.MapContainer;

public class DialogChat extends Chat {
    public long owner;

    @Override
    public String getTitle() {
        return "DIALOG TITLE";
    }

    @Override
    public void init(MapContainer map) {
        owner = map.getLongOr("owner", 0);
    }
}
