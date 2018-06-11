package com.thredUp.client;

import java.io.IOException;
import java.net.Socket;

import com.thredUp.common.Sender;
import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageType;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class ClientSender extends Sender {

    public ClientSender(Socket socket) throws IOException {
        super(socket);
    }

    public void ping() {
        Message ping = new Message()
            .messageType(MessageType.PING)
            .messageId();
        send(ping);
    }
}
