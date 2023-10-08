package com.example.jwt2.repository;

import com.example.jwt2.domain.APIUser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class APIUserRepositoryTests {

    @Autowired
    private APIUserRepository apiUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSaveApiUser() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            APIUser apiUser = APIUser.builder()
                    .mid("member" + String.valueOf(i))
                    .mpw(passwordEncoder.encode("1111"))
                    .build();

            this.apiUserRepository.save(apiUser);
        });
    }
}
