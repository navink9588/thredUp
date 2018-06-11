package com.thredUp.common.receiver;

import com.thredUp.common.model.Message;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public interface MessageHandler {
    void handle(Message message);
}
