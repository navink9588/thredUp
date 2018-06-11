package com.thredUp.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.thredUp.common.model.CommandType;
import com.thredUp.common.model.Message;
import com.thredUp.common.model.MessageType;
import com.thredUp.common.receiver.Receiver;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class Client {
    private static Logger LOG = Logger.getLogger(Client.class);
    public static Boolean connected = true;

    private ClientSender sender;
    private Thread receiverThread;
    private Handler handler;

    public Client() throws IOException {
        ClientProperties clientProperties = new ClientProperties();
        Socket socket = new Socket(clientProperties.getServerHost(), clientProperties.getPort());
        sender = new ClientSender(socket);

        handler = new Handler();
        Receiver receiver = new Receiver(socket, handler);
        receiverThread = new Thread(receiver);
        receiverThread.start();

        startSending();
    }

    private void startSending() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if(!isConnected()) {
                break;
            }
            System.out.println("Select message type:\n\t"
                + "1. Ping\n\t"
                + "2. Request\n\n"
                + "Press 0 to quit.\n");
            Integer option = scanner.nextInt();
            if(!isConnected()) {
                break;
            }
            if(option == 0) {
                receiverThread.interrupt();
                break;
            }
            try {
                MessageType messageType = MessageType.fromValue(option);
                switch (messageType) {
                    case PING:
                        sender.ping();
                        break;
                    case REQUEST:
                        System.out.println("Select request type: \n\t"
                            + "1. Status Check\n\t"
                            + "2. Init device\n\t"
                            + "3. Re-Init device\n\t"
                            + "4. Enable device\n\t"
                            + "5. Disable device\n");
                        option = scanner.nextInt();
                        CommandType commandType = CommandType.fromValue(option);
                        System.out.println("Enter Integer device Id: ");
                        option = scanner.nextInt();
                        if(!isConnected()) {
                            break;
                        }
                        Message message = new Message()
                            .messageType(messageType)
                            .messageId()
                            .commandType(commandType)
                            .deviceId(option);
                        handler.addPending(message);
                        sender.send(message);
                        break;
                    case RESPONSE:
                        LOG.error("Client can't send response. Please retry");
                        break;
                }
            } catch (IllegalArgumentException ex) {
                LOG.error("Invalid option selected. Please retry.");
            }
        }
    }

    private boolean isConnected() {
        if(!connected) {
            LOG.info("Server disconnected. Shutting down now.");
            receiverThread.interrupt();
        }
        return connected;
    }
}
