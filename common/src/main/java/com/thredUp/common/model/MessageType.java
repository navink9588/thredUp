package com.thredUp.common.model;

import java.util.Objects;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public enum MessageType {
    PING(1),
    REQUEST(2),
    RESPONSE(3),
    DISCONNECT(4);

    private Integer value;

    MessageType(Integer value) {
        this.value = value;
    }

    public Integer toValue() {
        return this.value;
    }

    public static MessageType fromValue(Integer value) {
        if(value != null) {
            for (MessageType enumValue : MessageType.values()) {
                if (Objects.equals(enumValue.value, value)) {
                    return enumValue;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Invalid value [%d] for MessageType.", value));
    }
}
