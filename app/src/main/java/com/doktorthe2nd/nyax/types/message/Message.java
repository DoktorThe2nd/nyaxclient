package com.doktorthe2nd.nyax.types.message;

import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.Profile;

import java.util.HashMap;

/** "Legacy". Message with no content. Should be extended. */
public class Message {
    // attempts
    public static Message attempts(MapContainer map) {
        if (map.containsKey("text")) return new MessageText(map);
        return new Message(map);
    }

    public final long cid = genCid(); // deduplication
    public MessageLink link = null; // may null
    public long id;
    public long senderId;
    public long time;

    public Message(MapContainer map) {
        id = map.getLongOr("id", 0);
        time = map.getLongOr("time", 0);
        senderId = map.getLongOr("sender", 0);

        if (map.getc("link").getStringOr("type", "").equals("REPLY")) {
            MapContainer msg = map.getc("link").getc("message");
            Long linkId = msg.getLong("id");
            String replyText = msg.getString("text");
            if (linkId != null) setReplyTo(linkId, replyText);
        }
    }

    public boolean isMine() {
        return senderId == Profile.me.getId();
    }

    /** Should be overridden.
     * <pre>{@code
     * @Override
     * public MapContainer serialize() {
     *     return super.serialize()
     *         .putc("myData", data)
     *         .putc("myOtherData", otherData);
     * }
     * }</pre>*/
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

    public void setReplyTo(long messageId, String replyText) {
        link = MessageLink.reply(messageId, replyText);
    }
}
