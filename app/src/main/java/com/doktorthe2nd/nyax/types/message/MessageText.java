package com.doktorthe2nd.nyax.types.message;

import com.doktorthe2nd.nyax.types.MapContainer;

import java.util.ArrayList;
import java.util.List;

public class MessageText extends Message {
    public String text;
    public List<String> elements = new ArrayList<>();
    public List<String> attaches = new ArrayList<>();

    public MessageText(MapContainer map) {
        super(map);
        text = map.getStringOr("text", "<null msg text>");
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("text", text)
                .putc("elements", elements)
                .putc("attaches", attaches);
    }
}
