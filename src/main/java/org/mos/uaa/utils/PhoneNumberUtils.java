package org.mos.uaa.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PhoneNumberUtils {

    public String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+84")) {
            return "84" + phoneNumber.substring(3);
        }

        if (phoneNumber.startsWith("0")) {
            return "84" + phoneNumber.substring(1);
        }

        return phoneNumber;
    }

}
