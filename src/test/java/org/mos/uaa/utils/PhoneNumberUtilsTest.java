package org.mos.uaa.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PhoneNumberUtilsTest {

    @Test
    void normalizePhoneNumber_withPlus84Prefix_shouldConvertCorrectly() {
        String result = PhoneNumberUtils.normalizePhoneNumber("+84912345678");
        assertEquals("84912345678", result);
    }

    @Test
    void normalizePhoneNumber_withZeroPrefix_shouldConvertCorrectly() {
        String result = PhoneNumberUtils.normalizePhoneNumber("0912345678");
        assertEquals("84912345678", result);
    }

    @Test
    void normalizePhoneNumber_with84Prefix_shouldRemainUnchanged() {
        String result = PhoneNumberUtils.normalizePhoneNumber("84912345678");
        assertEquals("84912345678", result);
    }

    @Test
    void normalizePhoneNumber_withInvalidFormat_shouldReturnAsIs() {
        String result = PhoneNumberUtils.normalizePhoneNumber("123456789");
        assertEquals("123456789", result);
    }

    @Test
    void normalizePhoneNumber_withEmptyString_shouldReturnEmpty() {
        String result = PhoneNumberUtils.normalizePhoneNumber("");
        assertEquals("", result);
    }

    @Test
    void normalizePhoneNumber_withNull_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            PhoneNumberUtils.normalizePhoneNumber(null);
        });
    }

}
