package com.doktorthe2nd.min.modules.chat;

import com.doktorthe2nd.min.types.MapContainer;

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
