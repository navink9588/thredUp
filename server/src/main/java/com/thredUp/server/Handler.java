package com.thredUp.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

import com.thredUp.common.Sender;
import com.thredUp.common.model.CommandStatus;
import com.thredUp.common.model.DeviceStatus;
import com.thredUp.common.model.ErrorCode;
import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageType;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public class Handler implements Runnable {
    private static Logger LOG = Logger.getLogger(Handler.class);

    private Map<Integer, DeviceStatus> devices;

    private LinkedBlockingDeque<Message> queue;
    private Sender sender;

    public Handler(LinkedBlockingDeque<Message> queue, ServerProperties properties) {
        this.queue = queue;
        this.sender = null;
        initDeviceStatus(properties.getDeviceCount());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = queue.takeFirst();
                if(message != null) {
                    MessageType messageType = message.getMessageType();
                    if(messageType == MessageType.REQUEST) {
                        handleRequest(message);
                    } else {
                        LOG.error("Server does not handle " + messageType + " messages");
                    }
                } else {
                    break;
                }
            } catch (InterruptedException ex) {
                LOG.warn("Handler was interrupted while waiting to get Message from queue. Trying again.");
            }
        }
    }

    private void handleRequest(Message message) {
        if(message.getMessageType() == MessageType.DISCONNECT) {
            LOG.error("Server does not handler disconnect message.");
            return;
        }
        Message response = new Message()
            .messageType(MessageType.RESPONSE)
            .messageId(message.getMessageId())
            .commandType(message.getCommandType());

        Integer dId = message.getDeviceId();

        if(!devices.containsKey(dId)) {
            response.commandStatus(CommandStatus.FAILURE)
                .errorCode(ErrorCode.DEVICE_NOT_FOUND);
        } else {
            switch (message.getCommandType()) {
                case STATUS:
                    response.commandStatus(CommandStatus.SUCCESS)
                        .deviceStatus(devices.get(dId));
                    break;
                case INITIALIZE:
                    boolean alreadyInitialized =
                        checkIfAlreadyInState(devices.get(dId), DeviceStatus.INITIALIZED, response,
                            ErrorCode.ALREADY_INITIALIZED);
                    if(!alreadyInitialized) {
                        handleRequestRandomly(response);
                    }
                    break;
                case REINITIALIZE:
                    handleRequestRandomly(response);
                    break;
                case ENABLE:
                    boolean alreadyEnabled =
                        checkIfAlreadyInState(devices.get(dId), DeviceStatus.ENABLED, response,
                            ErrorCode.ALREADY_ENABLED);
                    if(!alreadyEnabled) {
                        handleRequestRandomly(response);
                    }
                    break;
                case DISABLE:
                    boolean alreadyDisabled =
                        checkIfAlreadyInState(devices.get(dId), DeviceStatus.DISABLED, response,
                            ErrorCode.ALREADY_DISABLED);
                    if(!alreadyDisabled) {
                        handleRequestRandomly(response);
                    }
                    break;
            }
        }

        sender.send(response);
    }

    private boolean checkIfAlreadyInState(DeviceStatus currentStatus, DeviceStatus requestedStatus,
        Message response, ErrorCode errorCode) {
        if(currentStatus == requestedStatus) {
            response.commandStatus(CommandStatus.FAILURE)
                .errorCode(errorCode);
            return true;
        }
        return false;
    }

    private void handleRequestRandomly(Message response) {
        CommandStatus status = CommandStatus.randomCommandStatus();
        response.commandStatus(status);
        if(status == CommandStatus.FAILURE) {
            response.errorCode(ErrorCode.randomErrorCode());
        }
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void reset() {
        this.sender = null;
    }

    private void initDeviceStatus(int deviceCount) {
        devices = new HashMap<>();
        for(int i = 1; i <= deviceCount; i++) {
            devices.put(i, DeviceStatus.randomDeviceStatus());
        }
    }
}
