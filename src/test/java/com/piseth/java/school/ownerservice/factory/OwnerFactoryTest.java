package com.piseth.java.school.ownerservice.factory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.domain.enums.OwnerStatus;

class OwnerFactoryTest {

    @Test
    void newPendingOwner_shouldCreateNewOwnerWithPendingStatusAndAuditFields_andNotMutateDraft() {
        // Given: fixed time for deterministic test
        Instant fixedNow = Instant.parse("2026-01-01T00:00:00Z");
        Clock fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);

        OwnerFactory factory = new OwnerFactory(fixedClock);

        UUID draftId = UUID.randomUUID();
        Owner draft = Owner.builder()
            .id(draftId)
            .email("test@example.com")
            .phone("012 345")
            // even if someone sets status/time in draft, factory must enforce its own rules
            .status(OwnerStatus.ACTIVE)
            .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
            .updatedAt(Instant.parse("2025-01-02T00:00:00Z"))
            .build();

        // Then
        Owner result = factory.newPendingOwner(draft);

        // Assert: new instance (no mutation / no reuse)
        assertNotSame(draft, result);

        // Assert: copies identity/contact fields
        assertEquals(draftId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("012 345", result.getPhone());

        // Assert: factory enforces lifecycle rule
        assertEquals(OwnerStatus.PENDING, result.getStatus());

        // Assert: audit fields initialized from clock
        assertEquals(fixedNow, result.getCreatedAt());
        assertEquals(fixedNow, result.getUpdatedAt());

        // Assert: draft object not modified
        assertEquals(OwnerStatus.ACTIVE, draft.getStatus());
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), draft.getCreatedAt());
        assertEquals(Instant.parse("2025-01-02T00:00:00Z"), draft.getUpdatedAt());
    }

    @Test
    void newPendingOwner_shouldAllowNullId_becauseDbMayGenerateIt() {
        // Arrange
        Instant fixedNow = Instant.parse("2026-01-01T00:00:00Z");
        Clock fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);

        OwnerFactory factory = new OwnerFactory(fixedClock);

        Owner draft = Owner.builder()
            .id(null)
            .email("test@example.com")
            .phone(null)
            .build();

        // Act
        Owner result = factory.newPendingOwner(draft);

        // Assert
        assertNull(result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertNull(result.getPhone());
        assertEquals(OwnerStatus.PENDING, result.getStatus());
        assertEquals(fixedNow, result.getCreatedAt());
        assertEquals(fixedNow, result.getUpdatedAt());
    }
}