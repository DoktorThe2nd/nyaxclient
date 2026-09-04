package com.doktorthe2nd.nyax.net;

@FunctionalInterface
public interface OnReply {
    void apply(Packet packet);
}