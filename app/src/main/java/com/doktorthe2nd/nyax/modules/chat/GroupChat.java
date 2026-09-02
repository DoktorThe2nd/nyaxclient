package com.doktorthe2nd.nyax.modules.chat;

import com.doktorthe2nd.nyax.types.MapContainer;

public class GroupChat extends Chat {
    private String title;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void init(MapContainer map) {
        title = map.getStringOr("title", "<title is null>");
    }
}
