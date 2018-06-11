package com.thredUp.common.model;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public enum CommandStatus {
    SUCCESS(1),
    FAILURE(2);

    private Integer value;

    CommandStatus(Integer value) {
        this.value = value;
    }

    public Integer toValue() {
        return this.value;
    }

    public static CommandStatus fromValue(Integer value) {
        if(value != null) {
            for (CommandStatus enumValue : CommandStatus.values()) {
                if (Objects.equals(enumValue.value, value)) {
                    return enumValue;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Invalid value [%d] for CommandStatus.", value));
    }

    public static CommandStatus randomCommandStatus() {
        return fromValue(ThreadLocalRandom.current().nextInt(1, 3));
    }
}
