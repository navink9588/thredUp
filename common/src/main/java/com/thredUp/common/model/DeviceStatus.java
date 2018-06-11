package com.thredUp.common.model;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Navin Kumar
 * @date 6/9/18
 */
public enum DeviceStatus {
    INITIALIZED(1),
    ENABLED(2),
    DISABLED(3);

    private Integer value;

    DeviceStatus(Integer value) {
        this.value = value;
    }

    public Integer toValue() {
        return this.value;
    }

    public static DeviceStatus fromValue(Integer value) {
        if(value != null) {
            for (DeviceStatus enumValue : DeviceStatus.values()) {
                if (Objects.equals(enumValue.value, value)) {
                    return enumValue;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Invalid value [%d] for DeviceStatus.", value));
    }

    public static DeviceStatus randomDeviceStatus() {
        return fromValue(ThreadLocalRandom.current().nextInt(1, 4));
    }
}
