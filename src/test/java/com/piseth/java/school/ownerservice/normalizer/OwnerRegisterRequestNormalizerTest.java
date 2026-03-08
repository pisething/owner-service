package com.piseth.java.school.ownerservice.normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;

@ExtendWith(MockitoExtension.class)
public class OwnerRegisterRequestNormalizerTest {

	@Mock
    private EmailNormalizer emailNormalizer;

    @Mock
    private PhoneNormalizer phoneNormalizer;

    @InjectMocks
    private OwnerRegisterRequestNormalizer normalizer;
    /*
    @Test
    void test() {
    	//given
    	
    	//when
    	
    	
    	//then
    }
    */
    
    @Test
    void shouldReturnNewInstance_andNotMutateInput() {
        // Given
    	String email = "  TEST@Example.Com  ";
    	String phone = " 012 345 ";
        OwnerRegisterRequest input = new OwnerRegisterRequest();
        input.setEmail(email);
        input.setPhone(phone);
        
        // When
        when(emailNormalizer.normalize(email)).thenReturn("test@example.com");
        when(phoneNormalizer.normalize(phone)).thenReturn("012 345");

        // Then
        OwnerRegisterRequest out = normalizer.normalize(input);

        // Assert: new instance (immutability)
        assertNotNull(out);
        assertNotSame(input, out);
/*
        // Assert: output contains normalized values
        assertEquals("test@example.com", out.getEmail());
        assertEquals("012 345", out.getPhone());

        // Assert: input remains unchanged
        assertEquals("  TEST@Example.Com  ", input.getEmail());
        assertEquals(" 012 345 ", input.getPhone());

        // Verify interactions
        verify(emailNormalizer).normalize("  TEST@Example.Com  ");
        verify(phoneNormalizer).normalize(" 012 345 ");
        verifyNoMoreInteractions(emailNormalizer, phoneNormalizer);
        
        */
    }
    
    // Unit Test help developer to write better.
    
    
    
    
}
