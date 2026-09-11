package com.doktorthe2nd.nyax.types.chat;

import com.doktorthe2nd.nyax.modules.MReporter;
import com.doktorthe2nd.nyax.net.Connection;
import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.message.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Chat {
    private final Map<Long, Message> messages_map = new HashMap<>(); // optimization
    public List<Message> messages = new ArrayList<>();
    public Message lastMessage;

    public long id; // = id = cid
    public long lastChangeTime; // = modified

    public abstract String getTitle();
    public abstract void init(MapContainer map);

    public void downloadMessagesBackwards(int count) {
        downloadMessagesBackwards(count, lastMessage);
    }
    public void downloadMessagesBackwards(int count, Message from) {
        downloadMessagesBackwards(count, from.time);
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
                msgs.add(Message.attempts(MapContainer.of(msg_map)));
            }
            prependMessages(msgs);
        });
    }
    public void downloadMessagesForwards(int count, Message from) {
        downloadMessagesForwards(count, from.time);
    }
    public void downloadMessagesForwards(int count, long from) {
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
            if (MReporter.toastIfError(packet)) return;
            List<Message> msgs = new ArrayList<>();
            for (Map<Object, Object> msg_r_map : MapContainer.of(packet.payload).getMapsArray("messages")) {
                msgs.add(Message.attempts(MapContainer.of(msg_r_map)));
            }
            addMessages(msgs);
        });
    }

    public void prependMessages(List<Message> msgs) {
        messages.addAll(0, msgs);
        for (Message msg : msgs) {
            if (messages_map.put(msg.id, msg) != null)
                MReporter.toastError("Message duplication. Re-enter chat.");
        }
    }
    public void addMessages(List<Message> msgs) {
        messages.addAll(msgs);
        for (Message msg : msgs) {
            if (messages_map.put(msg.id, msg) != null)
                MReporter.toastError("Message duplication. Re-enter chat.");
        }
    }
    public void clear() {
        messages.clear();
        messages_map.clear();
    }

    public Message getMessageById(long id) {
        if (messages_map.containsKey(id)) return messages_map.get(id);
        return null; // TODO: download message
    }

    public static Chat attempts(MapContainer map) {
        String type = map.getStringOr("type", "null");
        Chat chat;

        switch (type) {
            case "DIALOG": chat = new DialogChat(); break;
            case "CHANNEL": chat = new ChannelChat(); break;
            case "CHAT": chat = new GroupChat(); break;
            default: throw new RuntimeException("Unknown chat type " + type + " in: " + map);
        }

        chat.id = map.getLongOr("id", map.getLongOr("cid", 0));
        chat.lastChangeTime = map.getLongOr("modified", 0);
        chat.lastMessage = Message.attempts(map.getc("lastMessage"));
        chat.init(map);

        return chat;
    }

    public static List<Chat> sortByTime(List<Chat> list) {
        list.sort(Comparator.comparingLong(v -> v.lastChangeTime));
        return list;
    }

    public static List<Chat> fromData(List<Map<Object, Object>> data) {
        return data.stream().map(v ->
                Chat.attempts(MapContainer.of(v))).collect(Collectors.toList());
    }

    public static List<Chat> emptyList() {
        return new ArrayList<>();
    }
}
