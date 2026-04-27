package com.piseth.java.school.ownerservice.normalizer;
import org.springframework.stereotype.Component;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerRegisterRequestNormalizer {

    private final EmailNormalizer emailNormalizer;
    private final PhoneNormalizer phoneNormalizer;

    public OwnerRegisterRequest normalize(OwnerRegisterRequest request) {
    	
    	OwnerRegisterRequest newRequest = new OwnerRegisterRequest();
    	
    	newRequest.setEmail(emailNormalizer.normalize(request.getEmail()));
    	newRequest.setPhone(phoneNormalizer.normalize(request.getPhone()));
        return newRequest;
    }
    
    public String normalizeEmail(String email) {
        return emailNormalizer.normalize(email);
    }

    public String normalizePhone(String phone) {
        return phoneNormalizer.normalize(phone);
    }
    
}

// @Mock

// value
// behavior

