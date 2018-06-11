package com.thredUp.common.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Navin Kumar
 * @date 6/10/18
 */
public class MessageTest {
    private static final Integer ID = 1;

    @Test
    public void testValidStatusRequestMessage() throws MessageException {
        /*
            2;1;1;1
            where, REQUEST  - 2
                   ID       - 1
                   STATUS   - 1
                   DeviceId - 1
         */
        String binaryMessage = "00110010001110110011000100111011001100010011101100110001";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.REQUEST);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.STATUS);
        assertEquals(message.getDeviceId(), ID);
        assertNull(message.getDeviceStatus());
        assertNull(message.getCommandStatus());
        assertNull(message.getErrorCode());
    }

    @Test
    public void testValidInitRequestMessage() throws MessageException {
        /*
            2;1;2;1
            where, REQUEST    - 2
                   ID         - 1
                   INITIALIZE - 2
                   DeviceId   - 1
         */
        String binaryMessage = "00110010001110110011000100111011001100100011101100110001";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.REQUEST);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.INITIALIZE);
        assertEquals(message.getDeviceId(), ID);
        assertNull(message.getDeviceStatus());
        assertNull(message.getCommandStatus());
        assertNull(message.getErrorCode());
    }

    @Test
    public void testValidReInitRequestMessage() throws MessageException {
        /*
            2;1;3;1
            where, REQUEST      - 2
                   ID           - 1
                   REINITIALIZE - 3
                   DeviceId     - 1
         */
        String binaryMessage = "00110010001110110011000100111011001100110011101100110001";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.REQUEST);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.REINITIALIZE);
        assertEquals(message.getDeviceId(), ID);
        assertNull(message.getDeviceStatus());
        assertNull(message.getCommandStatus());
        assertNull(message.getErrorCode());
    }

    @Test
    public void testValidEnableRequestMessage() throws MessageException {
        /*
            2;1;4;1
            where, REQUEST  - 2
                   ID       - 1
                   ENABLE   - 4
                   DeviceId - 1
         */
        String binaryMessage = "00110010001110110011000100111011001101000011101100110001";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.REQUEST);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.ENABLE);
        assertEquals(message.getDeviceId(), ID);
        assertNull(message.getDeviceStatus());
        assertNull(message.getCommandStatus());
        assertNull(message.getErrorCode());
    }

    @Test
    public void testValidDisableRequestMessage() throws MessageException {
        /*
            2;1;5;1
            where, REQUEST  - 2
                   ID       - 1
                   DISABLE  - 5
                   DeviceId - 1
         */
        String binaryMessage = "00110010001110110011000100111011001101010011101100110001";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.REQUEST);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.DISABLE);
        assertEquals(message.getDeviceId(), ID);
        assertNull(message.getDeviceStatus());
        assertNull(message.getCommandStatus());
        assertNull(message.getErrorCode());
    }

    @Test
    public void testValidSuccessStatusResponseMessage() throws MessageException {
        /*
            3;1;1;1;2
            where, RESPONSE - 3
                   ID       - 1
                   STATUS   - 1
                   SUCCESS  - 1
                   ENABLED  - 2
         */
        String binaryMessage = "001100110011101100110001001110110011000100111011001100010011101100110010";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.RESPONSE);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.STATUS);
        assertEquals(message.getCommandStatus(), CommandStatus.SUCCESS);
        assertEquals(message.getDeviceStatus(), DeviceStatus.ENABLED);
        assertNull(message.getDeviceId());
        assertNull(message.getErrorCode());
    }

    @Test
    public void testValidFailureStatusResponseMessage() throws MessageException {
        /*
            3;1;1;2;1
            where, RESPONSE - 3
                   ID       - 1
                   STATUS   - 1
                   FAILURE  - 2
                   DEVICE_NOT_FOUND - 1
         */
        String binaryMessage = "001100110011101100110001001110110011000100111011001100100011101100110001";
        Message message = new Message().fromBinary(binaryMessage);
        assertEquals(message.toBinary(), binaryMessage);
        assertEquals(message.getMessageType(), MessageType.RESPONSE);
        assertEquals(message.getMessageId(), ID);
        assertEquals(message.getCommandType(), CommandType.STATUS);
        assertEquals(message.getCommandStatus(), CommandStatus.FAILURE);
        assertEquals(message.getErrorCode(), ErrorCode.DEVICE_NOT_FOUND);
        assertNull(message.getDeviceId());
        assertNull(message.getDeviceStatus());
    }

    @Test(expected = MessageException.class)
    public void testInValidSuccessStatusResponseMessage() throws MessageException {
        /*
            Missing device status
            3;1;1;1
            where, RESPONSE - 3
                   ID       - 1
                   STATUS   - 1
                   SUCCESS  - 1
         */
        String binaryMessage = "00110011001110110011000100111011001100010011101100110001";
        new Message().fromBinary(binaryMessage);
    }

    @Test(expected = MessageException.class)
    public void testInvalidMessage() throws MessageException {
        Message message = new Message();
        message.toBinary();
    }

    @Test(expected = MessageException.class)
    public void testInvalidBinaryMessage_MissingBits() throws MessageException {
        new Message().fromBinary("0011001100111011001100010011101100110001001110110011000");
    }

    @Test(expected = MessageException.class)
    public void testInvalidBinaryMessage_Empty() throws MessageException {
        new Message().fromBinary("");
    }

    @Test(expected = MessageException.class)
    public void testInvalidBinaryMessage_Blank() throws MessageException {
        new Message().fromBinary(" ");
    }

    @Test(expected = MessageException.class)
    public void testInvalidBinaryMessage_NotBinary() throws MessageException {
        new Message().fromBinary("12");
    }

    @Test(expected = MessageException.class)
    public void testInvalidBinaryMessage_InvalidNumberFormat() throws MessageException {
        new Message().fromBinary("011000010011101100110001"); // "a;1"
    }
}
