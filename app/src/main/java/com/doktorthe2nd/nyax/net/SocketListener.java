package com.doktorthe2nd.nyax.net;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.MainActivity;
import com.doktorthe2nd.nyax.luaj.Events;
import com.doktorthe2nd.nyax.modules.MReporter;
import com.doktorthe2nd.nyax.net.exceptions.ExceedsBufferException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketListener {
    private final Socket socket;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread readerThread;

    public SocketListener(Socket socket) {
        this.socket = socket;
    }

    private void sendAnswerOnPing() {
        Connection.sendNoReply(OpcodeTable.ping, new HashMap<>(){{
            put("interactive", true);
        }});
    }

    public void start() {
        readerThread = new Thread(() -> {
            MainActivity.luajThread.callEvent(Events.SOCKET_OPENED);
            try (InputStream in = socket.getInputStream()) {
                System.out.println("SocketListener started");
                byte[] buffer = new byte[Consts.max_compressed_size];
                int bytesRead;
                int off = 0;
                while (running.get() && (bytesRead = in.read(buffer, off, buffer.length-off)) != -1) {
                    byte[] data = new byte[off+bytesRead];
                    System.arraycopy(buffer, 0, data, 0, off+bytesRead);
                    Packet packet;
                    try {
                        packet = PacketProcess.unpackPacket(data);
                    } catch (ExceedsBufferException e) {
                        off = bytesRead;
                        continue;
                    }
                    off = 0;
                    MReporter.log(packet.toString());
                    if (packet.opcode == OpcodeTable.ping && packet.cmd == Packet.CmdType.push)
                        sendAnswerOnPing();
                    else
                        Connection.popFromMap(packet.seq).apply(packet);
                }
            } catch (IOException e) {
                if (running.get()) {
                    throw new RuntimeException("IOException in SocketListener: "+e.getMessage());
                }
            } finally {
                running.set(false);
                System.out.println("SocketListener stopped");
                MainActivity.luajThread.callEvent(Events.SOCKET_CLOSED);
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void stop() {
        running.set(false);
        if (readerThread != null) {
            readerThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
