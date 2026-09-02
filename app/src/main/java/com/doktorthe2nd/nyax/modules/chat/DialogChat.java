package com.doktorthe2nd.nyax.modules.chat;

import com.doktorthe2nd.nyax.types.MapContainer;

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
