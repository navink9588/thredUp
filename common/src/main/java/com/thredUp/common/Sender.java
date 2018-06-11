package com.thredUp.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageException;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public class Sender {
    private static Logger LOG = Logger.getLogger(Sender.class);

    private PrintWriter writer;

    public Sender(Socket socket) throws IOException {
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(Message message) {
        String binaryMessage;
        try {
            binaryMessage = message.toBinary();
        } catch (MessageException e) {
            LOG.error("Failed to convert message to binary\n", e);
            return;
        }
        this.writer.println(binaryMessage);
        this.writer.flush();
    }
}
