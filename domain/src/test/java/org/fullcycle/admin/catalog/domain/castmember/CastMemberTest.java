package org.fullcycle.admin.catalog.domain.castmember;

import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CastMemberTest {

    @Test
    void givenAValidParams_whenCallsNewMember_shouldReturnCastMemberCreated() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actual = CastMember.newMember(expectedName, expectedType);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedType, actual.getType());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedType = CastMemberType.ACTOR;

        final var actual = assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(null, expectedType),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(1, actual.getErrors().size());
        assertEquals("'name' should not be null", actual.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidBlankName_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedType = CastMemberType.ACTOR;

        final var actual = assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(" ", expectedType),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(1, actual.getErrors().size());
        assertEquals("'name' should not be empty", actual.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameWithLessThan3Characters_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedType = CastMemberType.ACTOR;

        final var actual = assertThrows(
            NotificationException.class,
            () -> CastMember.newMember("ZZ", expectedType),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(1, actual.getErrors().size());
        assertEquals("'name' must be between 3 and 255 characters", actual.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameWithGreaterThan255Characters_whenCallsNewMember_shouldReceiveANotification() {
        final var leftLimit = 97;
        final var limitRight = 122;
        final var targetStringLength = 256;
        final String expectedName = new Random().ints(leftLimit, limitRight + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        final var expectedType = CastMemberType.ACTOR;

        final var actual = assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(expectedName, expectedType),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(1, actual.getErrors().size());
        assertEquals("'name' must be between 3 and 255 characters", actual.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidType_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedName = "Vin Diesel";

        final var actual = assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(expectedName, null),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(1, actual.getErrors().size());
        assertEquals("'type' should not be null", actual.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidLengthNameAndInvalidType_whenCallsNewMember_shouldReceiveANotification() {
        final var actual = assertThrows(
            NotificationException.class,
            () -> CastMember.newMember("ZZ", null),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(2, actual.getErrors().size());
        assertEquals("'name' must be between 3 and 255 characters", actual.getErrors().get(0).message());
        assertEquals("'type' should not be null", actual.getErrors().get(1).message());
    }

}
