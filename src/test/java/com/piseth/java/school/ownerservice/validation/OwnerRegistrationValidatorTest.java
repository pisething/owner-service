package com.piseth.java.school.ownerservice.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import com.piseth.java.school.ownerservice.exception.BadRequestException;
import com.piseth.java.school.ownerservice.repository.OwnerRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class OwnerRegistrationValidatorTest {

	@Mock
    private OwnerRepository ownerRepository;
	
	@InjectMocks
	private OwnerRegistrationValidator ownerRegistrationValidator;

//	
//    private OwnerRegistrationValidator validator() {
//        return new OwnerRegistrationValidator(ownerRepository);
//    }

    private static OwnerRegisterRequest req(String email, String phone) {
        OwnerRegisterRequest r = new OwnerRegisterRequest();
        r.setEmail(email);
        r.setPhone(phone);
        return r;
    }
    // Required Contact
    
    @Nested
    class ValidateRequiredContact {
    	@Test
        void shouldErrorSynchronously_whenBothEmailAndPhoneMissing() {
            // Given
            OwnerRegisterRequest request = req(null, null);

            // When + Then
            // Important: error is thrown before a Mono is even returned (synchronous validation).
            assertThrows(BadRequestException.class, () -> ownerRegistrationValidator.validate(request));

            // No DB interaction should happen
            verifyNoInteractions(ownerRepository);
        }
        // @TODO fix sonaqube recommend (return object exception)
        
        
        @Test
        void shouldPassRequiredContact_whenEmailProvided() {
            // Given
            OwnerRegisterRequest request = req("test@example.com", null);

            // When
            when(ownerRepository.existsByEmail("test@example.com")).thenReturn(Mono.just(false));
            // phone is null -> phone check should not call repository

            // Act + Assert
            StepVerifier.create(ownerRegistrationValidator.validate(request))
                .verifyComplete();

            verify(ownerRepository).existsByEmail("test@example.com");
            verify(ownerRepository, never()).existsByPhone(any());
            verifyNoMoreInteractions(ownerRepository);
        }
        
        @Test
        void shouldPassRequiredContact_whenPhoneProvided() {
            // Arrange
            OwnerRegisterRequest request = req(null, "012 345");

            when(ownerRepository.existsByPhone("012 345")).thenReturn(Mono.just(false));
            // email is null -> email check should not call repository

            // Act + Assert
            StepVerifier.create(ownerRegistrationValidator.validate(request))
                .verifyComplete();

            verify(ownerRepository).existsByPhone("012 345");
            verify(ownerRepository, never()).existsByEmail(any());
            verifyNoMoreInteractions(ownerRepository);
        }
    }
    
    
    
    // Uniqueness
    @Nested
    class ValidateUniqueness {
    	@Test
        void shouldComplete_whenEmailAndPhoneAreUnique() {
            // When
            OwnerRegisterRequest request = req("test@example.com", "012 345");

            when(ownerRepository.existsByEmail("test@example.com")).thenReturn(Mono.just(false));
            when(ownerRepository.existsByPhone("012 345")).thenReturn(Mono.just(false));

            // When + Then
            StepVerifier.create(ownerRegistrationValidator.validate(request))
                .verifyComplete();

            // Verify order: email check first, then phone check
            InOrder inOrder = inOrder(ownerRepository);
            inOrder.verify(ownerRepository).existsByEmail("test@example.com");
            inOrder.verify(ownerRepository).existsByPhone("012 345");
            //verify(ownerRepository).existsByEmail("test@example.com");
            //verify(ownerRepository).existsByPhone("012 345");
            verifyNoMoreInteractions(ownerRepository);
        }
    	
    	 @Test
         void shouldError_whenEmailAlreadyRegistered_andShouldNotCheckPhone() {
             // Given
             OwnerRegisterRequest request = req("dup@example.com", "012 345");

             // When
             when(ownerRepository.existsByEmail("dup@example.com")).thenReturn(Mono.just(true));

             // Then
             StepVerifier.create(ownerRegistrationValidator.validate(request))
                 .expectErrorSatisfies(ex -> {
                     assertTrue(ex instanceof BadRequestException);
                     assertEquals("Email already registered.", ex.getMessage());
                 })
                 .verify();

             // Critical rule: phone check must NOT run if email fails
             verify(ownerRepository).existsByEmail("dup@example.com");
             verify(ownerRepository, never()).existsByPhone(any());
             //verifyNoMoreInteractions(ownerRepository);
         }
    	 
    	 @Test
         void shouldError_whenPhoneAlreadyRegistered() {
             // Given
             OwnerRegisterRequest request = req("ok@example.com", "dup-phone");

             // When
             when(ownerRepository.existsByEmail("ok@example.com")).thenReturn(Mono.just(false));
             when(ownerRepository.existsByPhone("dup-phone")).thenReturn(Mono.just(true));

             // Then
             StepVerifier.create(ownerRegistrationValidator.validate(request))
                 .expectErrorSatisfies(ex -> {
                     assertTrue(ex instanceof BadRequestException);
                     assertEquals("Phone already registered.", ex.getMessage());
                 })
                 .verify();

             verify(ownerRepository).existsByEmail("ok@example.com");
             verify(ownerRepository).existsByPhone("dup-phone");
             //verifyNoMoreInteractions(ownerRepository);
         }
    	 
    	 @Test
         void shouldSkipEmailCheck_whenEmailBlank_andOnlyCheckPhone() {
             // Given
             OwnerRegisterRequest request = req("   ", "012 345");
             // When
             when(ownerRepository.existsByPhone("012 345")).thenReturn(Mono.just(false));

             // Then
             StepVerifier.create(ownerRegistrationValidator.validate(request))
                 .verifyComplete();

             verify(ownerRepository, never()).existsByEmail(any());
             verify(ownerRepository).existsByPhone("012 345");
             //verifyNoMoreInteractions(ownerRepository);
         }
    	 
    	 @Test
         void shouldPropagateRepositoryError() {
             // Given
             OwnerRegisterRequest request = req("test@example.com", null);
             // When
             RuntimeException dbError = new RuntimeException("db down");
             when(ownerRepository.existsByEmail("test@example.com")).thenReturn(Mono.error(dbError));

             // Then
             StepVerifier.create(ownerRegistrationValidator.validate(request))
                 .expectErrorSatisfies(ex -> assertSame(dbError, ex))
                 .verify();

             verify(ownerRepository).existsByEmail("test@example.com");
             //verifyNoMoreInteractions(ownerRepository);
         }
    }
    
    

    
    
    
}
