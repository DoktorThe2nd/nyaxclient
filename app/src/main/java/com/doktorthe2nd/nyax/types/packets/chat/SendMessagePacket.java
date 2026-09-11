package com.doktorthe2nd.nyax.types.packets.chat;

import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.message.Message;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

public class SendMessagePacket extends SendablePacket {
    private Message msg;
    private long chat_id;
    private boolean silent;

    public Message getMsg() {
        return msg;
    }

    @Override
    public int getOpcode() {
        return OpcodeTable.msgSend;
    }

    public SendMessagePacket() {markNotSerializable();}
    public SendMessagePacket(Message message, long chat_id, boolean silent) {
        this.msg = message;
        this.chat_id = chat_id;
        this.silent = silent;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("message", msg.serialize())
                .putc("chatId", chat_id)
                .putc("notify", !silent);
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        msg = Message.attempts(data);
        return true;
    }
}
