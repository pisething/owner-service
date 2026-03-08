package com.piseth.java.school.ownerservice.normalizer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PhoneNormalizerTest {

    private final PhoneNormalizer normalizer = new PhoneNormalizer();

        @Test
        void shouldReturnNull_whenPhoneIsNull() {
            assertNull(normalizer.normalize(null));
        }

        @Test
        void shouldReturnNull_whenPhoneIsBlank() {
            assertNull(normalizer.normalize(""));
            assertNull(normalizer.normalize("   "));
        }

        @Test
        void shouldTrimWhitespace() {
            String result = normalizer.normalize(" 012345 ");
            assertEquals("012345", result);
        }

        @Test
        void shouldNotChangeInternalSpaces() {
            String result = normalizer.normalize(" 012 345 ");
            assertEquals("012 345", result);
        }

        @Test
        void shouldReturnSameValue_whenAlreadyClean() {
            String result = normalizer.normalize("012345");
            assertEquals("012345", result);
        }
}