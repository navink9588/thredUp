package com.thredUp.common.model;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Navin Kumar
 * @date 6/10/18
 */
public enum ErrorCode {
    DEVICE_NOT_FOUND(1),
    ALREADY_INITIALIZED(2),
    ALREADY_ENABLED(3),
    ALREADY_DISABLED(4),
    DEVICE_UNREACHABLE(5),
    DEVICE_NON_RESPONSIVE(6),
    UNKNOWN_ERROR(7);

    private Integer value;

    ErrorCode(Integer value) {
        this.value = value;
    }

    public Integer toValue() {
        return this.value;
    }

    public static ErrorCode fromValue(Integer value) {
        if(value != null) {
            for (ErrorCode enumValue : ErrorCode.values()) {
                if (Objects.equals(enumValue.value, value)) {
                    return enumValue;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Invalid value [%d] for ErrorCode.", value));
    }

    public static ErrorCode randomErrorCode() {
        return fromValue(ThreadLocalRandom.current().nextInt(5, 8));
    }
}
