package com.thredUp.common.receiver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.thredUp.common.model.CommandType;
import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageType;

/**
 * @author Navin Kumar
 * @date 6/10/18
 */
public class ReceiverTest {
    private static BufferedReader bufferedReader;

    @Before
    public void before() throws IOException {
        bufferedReader = mock(BufferedReader.class);
    }

    @Test
    public void testValidSocketMessage() throws IOException {
        /*
            2;1;1;1
            where, REQUEST  - 2
                   ID       - 1
                   STATUS   - 1
                   DeviceId - 1
         */
        String binaryMessage = "00110010001110110011000100111011001100010011101100110001";
        when(bufferedReader.readLine()).thenReturn(binaryMessage).thenReturn(null);

        final Message[] messageReceived = new Message[1];
        Receiver receiver = new Receiver(bufferedReader, (Message message) -> {
            messageReceived[0] = message;
        });
        receiver.run();
        Message expectedMessage = new Message()
            .messageType(MessageType.REQUEST)
            .messageId(1)
            .commandType(CommandType.STATUS)
            .deviceId(1);

        assertEquals(expectedMessage, messageReceived[0]);
    }

    // Test to make sure that binary message parsing exception is finally caught.
    // Nothing is being asserted. Receiver just logs the error as it does not know
    // what to do with it.
    @Test
    public void testMessageExceptionCaught() throws IOException {
        String binaryMessage = "some garbage message";
        when(bufferedReader.readLine()).thenReturn(binaryMessage).thenReturn(null);

        Receiver receiver = new Receiver(bufferedReader, (Message message) -> {});
        receiver.run();
    }

    // Test to make sure that handler's exception is finally caught.
    // Nothing is being asserted. Receiver just logs the error as it
    // does not know what to do with the exception.
    @Test
    public void testHandlerExceptionCaught() throws IOException {
        String binaryMessage = "00110010001110110011000100111011001100010011101100110001";
        when(bufferedReader.readLine()).thenReturn(binaryMessage).thenReturn(null);

        Receiver receiver = new Receiver(bufferedReader, (Message message) -> {
            throw new RuntimeException("Throw some uncaught exception");
        });
        receiver.run();
    }

}
