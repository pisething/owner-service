package com.piseth.java.school.ownerservice.service;

import java.util.UUID;

import com.piseth.java.school.ownerservice.dto.OwnerEmailRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerPhoneRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;

import reactor.core.publisher.Mono;

public interface OwnerService {

    //Mono<OwnerResponse> register(OwnerRegisterRequest request);
	
	Mono<OwnerResponse> registerByEmail(OwnerEmailRegisterRequest request);

    Mono<OwnerResponse> registerByPhone(OwnerPhoneRegisterRequest request);
    
    Mono<OwnerResponse> getById(UUID ownerId);

}