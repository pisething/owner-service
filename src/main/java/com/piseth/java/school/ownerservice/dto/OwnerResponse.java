package com.piseth.java.school.ownerservice.dto;

import java.time.Instant;
import java.util.UUID;

import com.piseth.java.school.ownerservice.domain.enums.OwnerStatus;

import lombok.Builder;

@Builder
public class OwnerResponse {

    UUID id;
    String email;
    String phone;
    OwnerStatus status;
    Instant createdAt;
    Instant updatedAt;
}

// Builder Pattern
