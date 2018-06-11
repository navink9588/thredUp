package com.thredUp.server;

import java.io.IOException;
import java.net.Socket;

import com.thredUp.common.Sender;
import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageType;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class ServerSender extends Sender {
    public ServerSender(Socket socket) throws IOException {
        super(socket);
    }

    public void sendDisconnected() {
        Message disconnect = new Message()
            .messageType(MessageType.DISCONNECT)
            .messageId();
        send(disconnect);
    }
}
