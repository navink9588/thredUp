package com.thredUp.common.model;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public class Message {
    private static Logger LOG = Logger.getLogger(Message.class);

    private static final String DELIMITER = ";";
    private static Integer messageIdCount = 1;

    private MessageType messageType;
    private Integer messageId;
    private CommandType commandType;
    private Integer deviceId;
    private DeviceStatus deviceStatus;
    private CommandStatus commandStatus;
    private ErrorCode errorCode;

    public MessageType getMessageType() {
        return messageType;
    }

    public Message messageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public Message messageId() {
        return messageId(messageIdCount++);
    }

    public Message messageId(Integer messageId) {
        this.messageId = messageId;
        return this;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Message commandType(CommandType commandType) {
        this.commandType = commandType;
        return this;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public Message deviceId(Integer deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public Message deviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
        return this;
    }

    public CommandStatus getCommandStatus() {
        return commandStatus;
    }

    public Message commandStatus(CommandStatus commandStatus) {
        this.commandStatus = commandStatus;
        return this;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Message errorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public Message fromBinary(String binaryMessage) throws MessageException {
        List<String> tokens = extractTokens(binaryMessage);
        parseTokens(tokens);
        return this;
    }

    private void parseTokens(List<String> tokens) throws MessageException {
        int tokenCount = tokens.size();
        validateTokenCount(tokenCount, 2, "Socket Message");
        messageType = MessageType.fromValue(toInteger(tokens.get(0)));
        messageId = toInteger(tokens.get(1));

        if(messageType != MessageType.PING && messageType != MessageType.DISCONNECT) {
            validateTokenCount(tokenCount, 4, "Request/Response");
            commandType = CommandType.fromValue(toInteger(tokens.get(2)));

            switch (messageType) {
                case REQUEST:
                    deviceId = toInteger(tokens.get(3));
                    break;
                case RESPONSE:
                    commandStatus = CommandStatus.fromValue(toInteger(tokens.get(3)));
                    if(commandStatus == CommandStatus.FAILURE) {
                        validateTokenCount(tokenCount, 5, "Failure Response");
                        errorCode = ErrorCode.fromValue(toInteger(tokens.get(4)));
                    } else if(commandType == CommandType.STATUS) {
                        validateTokenCount(tokenCount, 5, "Status Response");
                        deviceStatus = DeviceStatus.fromValue(toInteger(tokens.get(4)));
                    }
                    break;
            }
        }
    }

    private List<String> extractTokens(String binary) throws MessageException {
        if(isBlank(binary) || binary.length() % 8 != 0 || !isBinary(binary)) {
            String err = "Binary message can't be null/empty and should have multiple of 8 bits";
            LOG.error(err);
            throw new MessageException(err);
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < binary.length(); i += 8) {
            int charCode = Integer.parseInt(binary.substring(i, i+8), 2);
            result.append((char) charCode);
        }

        return Arrays.asList(result.toString().split(DELIMITER));
    }

    private boolean isBlank(String string) {
        if (string == null || string.isEmpty()) {
            return true;
        }
        for(char ch : string.toCharArray()) {
            if (!Character.isWhitespace(ch)) {
                return false;
            }
        }
        return true;
    }

    private boolean isBinary(String string) {
        char zero = '0';
        char one = '1';
        for(char ch : string.toCharArray()) {
            int isZero = Character.compare(ch, zero);
            int isOne = Character.compare(ch, one);
            if(isZero != 0 && isOne != 0) {
                return false;
            }
        }
        return true;
    }

    private Integer toInteger(String string) throws MessageException {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException ex) {
            String err = String.format("Failed to parse %s to int.", string);
            LOG.error(err);
            throw new MessageException(err);
        }
    }

    private void validateTokenCount(int actualCount, int expectedCount, String what)
        throws MessageException {
        if(actualCount < expectedCount) {
            String err = String.format("%s message should have at least %d tokens.", what, expectedCount);
            LOG.info(err);
            throw new MessageException(err);
        }
    }

    public String toBinary() throws MessageException {
        String message = toSocketMessage();

        String binResult = "";
        final String fourBitPrepend = "0000";
        final String twoBitPrepend = "00";
        final String bitPrepend = "0";
        byte[] charCodes = message.getBytes();

        for(byte ch : charCodes) {
            String binary = Integer.toBinaryString(ch);
            switch(binary.length()) {
                case 4 : binResult += fourBitPrepend + binary; break;
                case 6 : binResult += twoBitPrepend + binary; break;
                case 7 : binResult += bitPrepend + binary; break;
                default : binResult += binary;
            }
        }

        return binResult;
    }

    private String toSocketMessage() throws MessageException {
        StringBuilder socketMessage = new StringBuilder();

        validateToken(messageType, "Message Type can't be null");
        validateToken(messageId, "Message Id can't be null");

        socketMessage.append(messageType.toValue())
            .append(DELIMITER)
            .append(messageId);

        if(messageType != MessageType.PING && messageType != MessageType.DISCONNECT) {
            validateToken(commandType, "Command Type can't be null");
            socketMessage.append(DELIMITER)
                .append(commandType.toValue());

            switch (messageType) {
                case REQUEST:
                    validateToken(deviceId, "Device Id can't be null in a request message");
                    socketMessage.append(DELIMITER)
                        .append(deviceId);
                    break;
                case RESPONSE:
                    validateToken(commandStatus, "Command Status can't be null in a response message");
                    socketMessage.append(DELIMITER)
                        .append(commandStatus.toValue());

                    if(commandStatus == CommandStatus.FAILURE) {
                        validateToken(errorCode, "Error code can't be null in a failure response message");
                        socketMessage.append(DELIMITER)
                            .append(errorCode.toValue());
                    } else if(commandType == CommandType.STATUS) {
                        validateToken(deviceStatus, "Device Status can't be null in device status response message");
                        socketMessage.append(DELIMITER)
                            .append(deviceStatus.toValue());
                    }
            }
        }

        return socketMessage.toString();
    }

    private void validateToken(Object object, String err) throws MessageException {
        if(object == null)throw new MessageException(err);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MessageType : ").append(messageType)
            .append("\nMessageId : ").append(messageId);
        if(messageType != MessageType.PING) {
            builder.append("\nCommandType : ").append(commandType);
            if(messageType == MessageType.REQUEST) {
                builder.append("\nDeviceId : ").append(deviceId);
            } else {
                builder.append("\nCommandStatus : ").append(commandStatus);
                if(commandStatus == CommandStatus.FAILURE) {
                    builder.append("\nError: ").append(errorCode);
                } else if(commandType == CommandType.STATUS) {
                    builder.append("\nDeviceStatus : ").append(deviceStatus);
                }
            }
        }


        return builder.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Message message = (Message) o;

        if (messageType != message.messageType)
            return false;
        if (messageId != null ? !messageId.equals(message.messageId) : message.messageId != null)
            return false;
        if (commandType != message.commandType)
            return false;
        if (deviceId != null ? !deviceId.equals(message.deviceId) : message.deviceId != null)
            return false;
        if (deviceStatus != message.deviceStatus)
            return false;
        if (commandStatus != message.commandStatus)
            return false;
        return errorCode == message.errorCode;

    }

    @Override public int hashCode() {
        int result = messageType != null ? messageType.hashCode() : 0;
        result = 31 * result + (messageId != null ? messageId.hashCode() : 0);
        result = 31 * result + (commandType != null ? commandType.hashCode() : 0);
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + (deviceStatus != null ? deviceStatus.hashCode() : 0);
        result = 31 * result + (commandStatus != null ? commandStatus.hashCode() : 0);
        result = 31 * result + (errorCode != null ? errorCode.hashCode() : 0);
        return result;
    }
}
