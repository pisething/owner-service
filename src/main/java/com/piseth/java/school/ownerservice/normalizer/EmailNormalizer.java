package com.piseth.java.school.ownerservice.normalizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailNormalizer {

    public String normalize(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return email.trim().toLowerCase();
    }
}


// email null

/**
 email valid "dara@gmail.com"
 email null
 email blank
 email has space ex: " dara@gmail.com "
 email capital case ex: "DARa@gmail.com"
 */
