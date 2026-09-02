package com.doktorthe2nd.nyax.modules.chat;

import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.modules.MReporter;
import com.doktorthe2nd.nyax.modules.message.Message;
import com.doktorthe2nd.nyax.net.Connection;
import com.doktorthe2nd.nyax.net.OpcodeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Chat {
    private final Map<Long, Message> messages_map = new HashMap<>(); // optimization
    public List<Message> messages = new ArrayList<>();
    public Message lastMessage;

    public long id; // = id = cid
    public long lastChangeTime; // = modified

    public abstract String getTitle();
    public abstract void init(MapContainer map);

    public void downloadMessagesBackwards(int count) {
        downloadMessagesBackwards(count, lastMessage.time);
    }
    public void downloadMessagesBackwards(int count, long from) {
        Connection.sendRequest(OpcodeTable.chatHistory, new HashMap<>(){{
            put("chatId", id);
            put("forward", 0);
            put("backward", count);
            put("backwardTime", 0);
            put("forwardTime", 0);
            put("getChat", false);
            put("getMessages", true);
            put("from", from);
            put("itemType", "REGULAR");
            put("interactive", false);
        }}, packet -> {
            if (MReporter.toastIfError(packet)) return;
            List<Message> msgs = new ArrayList<>();
            for (Map<Object, Object> msg_map : MapContainer.of(packet.payload).getMapsArray("messages")) {
                msgs.add(Message.getFromData(MapContainer.of(msg_map)));
            }
            prependMessages(msgs);
        });
    }
    /*public void downloadMessagesForwards(int count, long from) {
        Connection.sendRequest(OpcodeTable.chatHistory, new HashMap<>(){{
            put("chatId", id);
            put("forward", count);
            put("backward", 0);
            put("backwardTime", 0);
            put("forwardTime", 0);
            put("getChat", false);
            put("getMessages", true);
            put("from", from);
            put("itemType", "REGULAR");
            put("interactive", false);
        }}, packet -> {
            List<Message> msgs = new ArrayList<>();
            for (Map<Object, Object> msg_r_map : MapContainer.of(packet.payload).getMapsArray("messages")) {
                msgs.add(Message.getFromData(MapContainer.of(msg_r_map)));
            }
            add
        });
    }*/

    public void prependMessages(List<Message> msgs) {
        messages.addAll(0, msgs);
        for (Message msg : msgs) {
            messages_map.put(msg.id, msg);
        }
    }
    public void addMessages(List<Message> msgs) {
        messages.addAll(msgs);
        for (Message msg : msgs) {
            messages_map.put(msg.id, msg);
        }
    }
    public void clear() {
        messages.clear();
        messages_map.clear();
    }

    public Message getMessageById(long id) {
        return messages_map.get(id);
    }

    public static Chat fromLogin(MapContainer map) {
        String type = map.getStringOr("type", "CHANNEL");
        Chat chat;

        switch (type) {
            case "DIALOG": chat = new DialogChat(); break;
            case "CHANNEL": chat = new ChannelChat(); break;
            case "CHAT": chat = new GroupChat(); break;
            default: throw new RuntimeException("Unknown chat type: " + type);
        }

        chat.id = map.getLongOr("id", map.getLongOr("cid", 0));
        chat.lastChangeTime = map.getLongOr("modified", 0);
        chat.lastMessage = Message.getFromData(map.getc("lastMessage"));
        chat.init(map);

        return chat;
    }
}
