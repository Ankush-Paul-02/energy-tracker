package com.paul.userservice;

import com.paul.userservice.data.entities.User;
import com.paul.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UserserviceApplicationTests {

    private final int numberOfUsers = 10;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testUsersToDB() {
        for (int i = 1; i <= numberOfUsers; i++) {
            User user = User.builder()
                    .username("username" + i)
                    .email("email" + i + "@example.com")
                    .address("address" + i)
                    .isAlerting(i % 2 == 0)
                    .energyAlertingThreshold(1000.0 * i)
                    .build();
            userRepository.save(user);
        }
        log.info("Users saved to database");
    }

}
