package com.doktorthe2nd.nyax.types.packets;

import com.doktorthe2nd.nyax.net.Connection;
import com.doktorthe2nd.nyax.net.OnReply;

/**
 * Abstract class for packets with {@link #send(Connection.OnReply)} and {@link #sendIgnoreReply()} functions.
 * Inheritors should implement {@link #getOpcode()}. This function will be called when packet sending invoked.
 */
public abstract class SendablePacket extends EmptyPacket {
    /** Should return opcode from OpcodeTable. */
    public abstract int getOpcode();

    /** Send request with given opcode and result of {@link #serialize()} as payload. When server replies, invoke onReply.
     * <p>If onReply is null, reply is ignored. */
    public void sendWithOpcode(int opcode, OnReply onReply) {
        Connection.sendRequest(opcode, serialize().getMap(), onReply);
    }

    /** Send request. When server replies, invoke onReply. */
    public void send(OnReply onReply) {
        sendWithOpcode(getOpcode(), onReply);
    }
    /** Send packet. Ignore if got reply. */
    public void sendIgnoreReply() {
        sendWithOpcode(getOpcode(), null);
    }
}
