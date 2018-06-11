package com.thredUp.common.model;

import java.util.Objects;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public enum CommandType {
    STATUS(1),
    INITIALIZE(2),
    REINITIALIZE(3),
    ENABLE(4),
    DISABLE(5);

    private Integer value;

    CommandType(Integer value) {
        this.value = value;
    }

    public Integer toValue() {
        return this.value;
    }

    public static CommandType fromValue(Integer value) {
        if(value != null) {
            for (CommandType enumValue : CommandType.values()) {
                if (Objects.equals(enumValue.value, value)) {
                    return enumValue;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Invalid value [%d] for CommandType.", value));
    }
}
