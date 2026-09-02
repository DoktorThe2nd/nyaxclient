package com.doktorthe2nd.nyax.modules.chat;

import com.doktorthe2nd.nyax.types.MapContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MChats {
    public static final List<Chat> chats = new ArrayList<>();
    private static Chat currentChat;

    public static Chat getCurrentChat() {
        return currentChat;
    }
    public static void setCurrentChat(Chat chat) {
        currentChat = chat;
        currentChat.downloadMessagesBackwards(50);
    }

    public static void addFromLogin(List<Map<Object, Object>> list) {
        for (var chat_data : list) {
            chats.add(Chat.fromLogin(MapContainer.of(chat_data)));
        }
    }
}
