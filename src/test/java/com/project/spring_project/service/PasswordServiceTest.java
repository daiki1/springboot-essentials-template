package com.project.spring_project.service;

import com.project.spring_project.entity.user.User;
import com.project.spring_project.utils.TestUserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("dev")
public class PasswordServiceTest {

    @Value("${security.pepper}")
    private String pepper;

    @Autowired
    private TestUserUtil testUserUtil;

    @Test
    void passwordHash_variesWithDifferentPeppers() {
        String password = "MySecurePass1!";
        String pepper1 = "PEPPER_ONE";
        String pepper2 = "PEPPER_TWO";

        String hash1 = testUserUtil.getPasswordService().encodePassword(password + pepper1);
        String hash2 = testUserUtil.getPasswordService().encodePassword(password + pepper2);

        assertNotEquals(hash1, hash2, "Hashes should differ for different peppers");
    }

    @Test
    void loginFailsWithIncorrectPepper() {
        String rawPassword = "MySecurePass1!";
        String correctPepper = pepper;
        String incorrectPepper = "WRONG_PEPPER";

        String storedHash = testUserUtil.getPasswordService().encodePassword(rawPassword + correctPepper);

        boolean matches = testUserUtil.getPasswordService().matches(rawPassword + incorrectPepper, storedHash);

        assertFalse(matches, "Login should fail with wrong pepper");
    }

    @Test
    void pepperIsNotStoredInDatabase() {
        testUserUtil.registerUserIfNotExists();

        Optional<User> user = testUserUtil.fingUserByUsername(testUserUtil.getTestUsername());
        assertTrue(user.isPresent());
        String storedPasswordHash = user.get().getPassword();

        assertFalse(storedPasswordHash.contains(pepper), "Pepper should not be stored in DB hash");

        testUserUtil.deleteTestUser();
    }
}
