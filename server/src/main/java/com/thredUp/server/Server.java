package com.thredUp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.thredUp.common.model.Message;
import com.thredUp.common.receiver.Receiver;

/**
 * @author Navin Kumar
 * @date 6/11/18
 */
public class Server {
    private static Logger LOG = Logger.getLogger(Server.class);

    private ServerSocket server;
    private LinkedBlockingDeque<Message> queue;
    private Handler handler;
    private ServerProperties properties;

    public Server() throws IOException {
        properties = new ServerProperties();
        this.server = new ServerSocket(properties.getPort(), 1);
        LOG.info(String.format("Server running at address '%s', port '%d'",
            server.getInetAddress().getHostAddress(), server.getLocalPort()));
        queue = new LinkedBlockingDeque<>();

        handler = new Handler(queue, properties);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(handler);
        service.shutdown();

        acceptConnection();
    }

    private void acceptConnection() {
        while (true) {
            try {
                LOG.info("Waiting for new connection ...");
                Socket socket = this.server.accept();
                ServerSender sender = new ServerSender(socket);
                handler.setSender(sender);

                Receiver receiver = new Receiver(socket,
                    (Message message) -> queue.addLast(message));
                Thread receiverThread = new Thread(receiver);
                receiverThread.start();

                while (true) {
                    try {
                        long msSinceLastActivity = receiver.msElapsedSinceLastActivity();
                        if(msSinceLastActivity >= properties.getTimeoutMs()) {
                            LOG.info("Closing socket due to inactivity for " +
                                (msSinceLastActivity/1000) + " secs");
                            sender.sendDisconnected();
                            receiverThread.interrupt();
                            socket.close();
                            break;
                        }
                        TimeUnit.MILLISECONDS.sleep(properties.getTimeoutMs() - msSinceLastActivity);
                    } catch (Exception ignore) {}
                }
            } catch (IOException ex) {
                LOG.error("Exception handling connection with client");
            } finally {
                LOG.info("Clearing up queued message from last connection.");
                queue.clear();
                handler.reset();
            }
        }
    }
}
