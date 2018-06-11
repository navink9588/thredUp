package com.thredUp.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageType;
import com.thredUp.common.receiver.MessageHandler;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class Handler implements MessageHandler {
    private static Logger LOG = Logger.getLogger(Handler.class);

    private Map<Integer, Message> pendingMessages;

    public Handler() {
        pendingMessages = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(Message message) {
        if(message.getMessageType() == MessageType.DISCONNECT) {
            LOG.info("Received disconnect message from server. Please press any number to shutdown client.");
            Client.connected = false;
        } else if(message.getMessageType() == MessageType.RESPONSE) {
            if(!pendingMessages.containsKey(message.getMessageId())) {
                LOG.error("No pending request found with Id " + message.getMessageId());
                return;
            }
            LOG.info("Response received for pending request with Id " + message.getMessageId());
            LOG.info(message.toString());
            removePending(message.getMessageId());
        } else {
            LOG.error("Client can only handle Responses. "
                + "Ignoring received message:\n" + message.toString());
        }
    }

    public void addPending(Message message) {
        pendingMessages.put(message.getMessageId(), message);
    }

    public void removePending(Integer messageId) {
        pendingMessages.remove(messageId);
    }
}
