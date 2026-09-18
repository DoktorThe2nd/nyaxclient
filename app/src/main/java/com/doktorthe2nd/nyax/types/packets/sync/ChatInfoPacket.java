package com.doktorthe2nd.nyax.types.packets.sync;

import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatInfoPacket extends SendablePacket {
    @Override
    public int getOpcode() {
        return OpcodeTable.chatInfo;
    }

    private final List<Long> chat_ids;
    private List<Map<Object, Object>> chats_data;

    public List<MapContainer> getChatsData() {
        return chats_data.stream().map(MapContainer::of).collect(Collectors.toList());
    }
    public MapContainer getOneChatData() {
        return MapContainer.of(chats_data.get(0));
    }

    public ChatInfoPacket() {
        this.chat_ids = List.of(0L);
    }
    public ChatInfoPacket(long chat) {
        this.chat_ids = List.of(chat);
    }
    public ChatInfoPacket(List<Long> chats) {
        this.chat_ids = chats;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("chatIds", chat_ids);
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        chats_data = data.getMapsArray("chats");
        return allNotNull(chats_data);
    }
}
