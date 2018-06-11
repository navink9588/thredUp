package com.thredUp.common.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageException;
import com.thredUp.common.model.MessageType;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public class Receiver implements Runnable{
    private static Logger LOG = Logger.getLogger(Receiver.class);

    private BufferedReader bufferedReader;
    private MessageHandler handler;
    private Long lastActivity;

    public Receiver(Socket socket, MessageHandler handler)
        throws IOException {
        this(new BufferedReader(new InputStreamReader(socket.getInputStream())), handler);
        LOG.info("Receiving from client: " + socket.getInetAddress().getHostAddress());
    }

    protected Receiver(BufferedReader bufferedReader, MessageHandler handler) {
        this.handler = handler;
        this.bufferedReader = bufferedReader;
        updateLastActivity();
    }

    public void run() {
        while (true) {
            try {
                String binaryMessage = bufferedReader.readLine();
                if(binaryMessage == null) break;
                updateLastActivity();
                Message message = new Message().fromBinary(binaryMessage);
                if(message.getMessageType() != MessageType.PING) {
                    this.handler.handle(message);
                }
            } catch (IOException ignore) {}
            catch (MessageException ex) {
                LOG.error("Failed to parse binary message\n", ex);
            } catch (Exception ex) {
                LOG.error("Handler threw unhandled exception\n", ex);
            }
        }
    }

    private void updateLastActivity() {
        this.lastActivity = System.currentTimeMillis();
    }

    public long msElapsedSinceLastActivity() {
        return System.currentTimeMillis() - lastActivity;
    }
}
