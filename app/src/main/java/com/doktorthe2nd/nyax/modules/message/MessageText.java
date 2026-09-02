package com.doktorthe2nd.nyax.modules.message;

import com.doktorthe2nd.nyax.types.MapContainer;

import java.util.ArrayList;
import java.util.List;

public class MessageText extends Message {
    public String text;
    public List<String> elements = new ArrayList<>();
    public List<String> attaches = new ArrayList<>();

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("text", text)
                .putc("elements", elements)
                .putc("attaches", attaches);
    }

    public static MessageText produce(String text) {
        MessageText msg = new MessageText();
        msg.text = text;
        return msg;
    }
}
