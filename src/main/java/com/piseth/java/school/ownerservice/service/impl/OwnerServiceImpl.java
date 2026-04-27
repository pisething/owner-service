package com.piseth.java.school.ownerservice.service.impl;


import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.piseth.java.school.ownerservice.domain.Owner;
import com.piseth.java.school.ownerservice.domain.enums.VerificationType;
import com.piseth.java.school.ownerservice.dto.OwnerEmailRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerPhoneRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.dto.OwnerResponse;
import com.piseth.java.school.ownerservice.exception.OwnerNotFoundException;
import com.piseth.java.school.ownerservice.factory.OwnerFactory;
import com.piseth.java.school.ownerservice.mapper.OwnerMapper;
import com.piseth.java.school.ownerservice.normalizer.OwnerRegisterRequestNormalizer;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;
import com.piseth.java.school.ownerservice.service.OwnerService;
import com.piseth.java.school.ownerservice.service.VerificationService;
import com.piseth.java.school.ownerservice.validation.OwnerRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService{
	private final OwnerRepository ownerRepository; // done
    private final OwnerMapper ownerMapper; // done
    private final OwnerFactory ownerFactory; // done
    private final OwnerRegistrationValidator registrationValidator; // done
    private final OwnerRegisterRequestNormalizer normalizer; // done
    private final VerificationService verificationService;
    

    @Override
    public Mono<OwnerResponse> register(OwnerRegisterRequest request) {
    	log.info("Owner registration requested");

        OwnerRegisterRequest normalized = normalizer.normalize(request);

        Owner draft = ownerMapper.toOwnerDraft(normalized);
        Owner pending = ownerFactory.newPendingOwner(draft);
        //pending.setEmail(pending.getEmail() + "2");
        return registrationValidator.validate(normalized)
            .then(Mono.defer(() -> ownerRepository.save(pending)))
            .flatMap(savedOwner -> sendOtpForAvailableChannels(savedOwner).thenReturn(savedOwner))
            .doOnSuccess(saved2 -> log.info("Owner registered successfully. ownerId={}", saved2.getId()))
            .map(ownerMapper::toResponse);
    }
    
    @Override
    public Mono<OwnerResponse> registerByEmail(OwnerEmailRegisterRequest request) {
        log.info("Owner email registration requested");

        OwnerRegisterRequest normalized = new OwnerRegisterRequest();
        normalized.setEmail(normalizer.normalizeEmail(request.getEmail()));
        normalized.setPhone(null);

        Owner draft = ownerMapper.toOwnerDraft(normalized);
        Owner pending = ownerFactory.newPendingOwner(draft);

        return registrationValidator.validate(normalized)
            .then(Mono.defer(() -> ownerRepository.save(pending)))
            .flatMap(savedOwner -> verificationService
                .sendOtp(savedOwner.getId(), VerificationType.EMAIL)
                .thenReturn(savedOwner)
            )
            .doOnSuccess(savedOwner -> log.info(
                "Owner registered by email successfully. ownerId={}",
                savedOwner.getId()
            ))
            .map(ownerMapper::toResponse);
    }

    @Override
    public Mono<OwnerResponse> registerByPhone(OwnerPhoneRegisterRequest request) {
        log.info("Owner phone registration requested");

        OwnerRegisterRequest normalized = new OwnerRegisterRequest();
        normalized.setEmail(null);
        normalized.setPhone(normalizer.normalizePhone(request.getPhone()));

        Owner draft = ownerMapper.toOwnerDraft(normalized);
        Owner pending = ownerFactory.newPendingOwner(draft);

        return registrationValidator.validate(normalized)
            .then(Mono.defer(() -> ownerRepository.save(pending)))
            .flatMap(savedOwner -> verificationService
                .sendOtp(savedOwner.getId(), VerificationType.PHONE)
                .thenReturn(savedOwner)
            )
            .doOnSuccess(savedOwner -> log.info(
                "Owner registered by phone successfully. ownerId={}",
                savedOwner.getId()
            ))
            .map(ownerMapper::toResponse);
    }



	@Override
    public Mono<OwnerResponse> getById(UUID ownerId) {
        return ownerRepository.findById(ownerId)
            .switchIfEmpty(Mono.error(new OwnerNotFoundException(ownerId)))
            .map(ownerMapper::toResponse);
    }
	
	private Mono<Void> sendOtpForAvailableChannels(Owner owner) {
        Mono<Void> emailOtpMono = Mono.empty();
        Mono<Void> phoneOtpMono = Mono.empty();

        if (StringUtils.hasText(owner.getEmail())) {
            emailOtpMono = verificationService.sendOtp(owner.getId(), VerificationType.EMAIL);
        }

        if (StringUtils.hasText(owner.getPhone())) {
            phoneOtpMono = verificationService.sendOtp(owner.getId(), VerificationType.PHONE);
        }

        return emailOtpMono.then(phoneOtpMono);
    }


}
