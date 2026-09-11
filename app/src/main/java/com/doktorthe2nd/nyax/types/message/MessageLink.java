package com.doktorthe2nd.nyax.types.message;

public class MessageLink {
    public String type; // "REPLY" / "FORWARD"
    public long messageId;
    public String replyText; // type=REPLY only, should not be sent to server
    public long forwardToChatId; // "chatId", type=FORWARD only

    private MessageLink() {}

    public static MessageLink reply(long messageId, String replyText) {
        MessageLink ret = new MessageLink();
        ret.messageId = messageId;
        ret.type = "REPLY";
        ret.replyText = replyText;
        return ret;
    }

    public static MessageLink forward(long messageId, long toChatId) {
        MessageLink ret = new MessageLink();
        ret.messageId = messageId;
        ret.type = "FORWARD";
        ret.forwardToChatId = toChatId;
        return ret;
    }
}
