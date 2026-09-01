package com.doktorthe2nd.min.modules.message;

import com.doktorthe2nd.min.types.MapContainer;
import com.doktorthe2nd.min.modules.Profile;

import java.util.HashMap;

public class Message {
    public final long cid = genCid(); // deduplication
    public MessageLink link = null; // may null
    public long id;
    public long senderId;
    public long time;

    public static Message getFromData(MapContainer map) {
        Message message;

        if (map.containsKey("text")) message = MessageText.produce(map.getString("text"));
        else message = new Message();

        message.id = map.getLongOr("id", 0);
        message.time = map.getLongOr("time", 0);
        message.senderId = map.getLongOr("sender", 0);

        if (map.containsKey("link") && map.getc("link").getStringOr("type", "").equals("REPLY")) {
            MapContainer msg = map.getc("link").getc("message");
            Long linkId = msg.getLong("id");
            String replyText = msg.getString("text");
            if (linkId != null) message.setReplyTo(linkId, replyText);
        }

        return message;
    }

    public boolean isMine() {
        return senderId == Profile.myProfile.getId();
    }

    public MapContainer serialize() {
        return new MapContainer(new HashMap<>(){{
            put("cid", cid);
            if (link != null) put("link", new HashMap<>(){{
                put("type", link.type);
                put("messageId", link.messageId);
                if (link.type.equals("FORWARD")) put("chatId", link.forwardToChatId);
            }});
        }});
    }

    private static long previous_cid = 0;
    private static long genCid() {
        return previous_cid = Math.max(System.currentTimeMillis(), previous_cid + 1);
    }

    public static Message forward(long messageId, int toChatId) {
        Message message = new Message();
        message.link = MessageLink.forward(messageId, toChatId);
        return message;
    }

    public void setReplyTo(long messageId, String replyText) {
        link = MessageLink.reply(messageId, replyText);
    }
}
