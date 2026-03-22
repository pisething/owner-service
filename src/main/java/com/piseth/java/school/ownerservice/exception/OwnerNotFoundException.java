package com.piseth.java.school.ownerservice.exception;

import java.util.UUID;

public class OwnerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public OwnerNotFoundException(UUID ownerId) {
        super("Owner not found: " + ownerId);
    }
}