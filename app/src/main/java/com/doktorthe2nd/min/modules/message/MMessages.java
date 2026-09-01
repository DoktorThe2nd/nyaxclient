package com.doktorthe2nd.min.modules.message;

import com.doktorthe2nd.min.modules.MReporter;
import com.doktorthe2nd.min.net.Connection;
import com.doktorthe2nd.min.net.OpcodeTable;

import java.util.HashMap;

public class MMessages {
    public static void sendMessage(Message message, long chat_id, boolean silent) {
        Connection.sendRequest(OpcodeTable.msgSend, new HashMap<>(){{
            put("chatId", chat_id);
            put("message", message.serialize());
            put("notify", !silent);
        }}, packet -> {
            if (MReporter.toastIfError(packet)) return;
            MReporter.toast("Message sent!");
        });
    }
}
