<<<<<<< HEAD
import cncs.academy.ess.service.TodoUserService;
import cncs.academy.ess.controller.AuthorizationMiddleware;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.cryptography.PBKDF2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static cncs.academy.ess.cryptography.PBKDF2.bytesToHex;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoUserServiceTest {

    private TodoUserService userService;
    private UserRepository userRepositoryMock;
    private AuthorizationMiddleware authMiddleware;

    //Executar antes de cada teste
    @BeforeEach
    void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        userService = new TodoUserService(userRepositoryMock);
        authMiddleware = new AuthorizationMiddleware(userRepositoryMock);
    }

    @Test
    void login_shouldReturnValidJWTTokenWhenCredentialsMatch() throws Exception {
        // Arrange
        String username = "alice";
        String password = "password123";

        // Gerar salt e hash para simular user real
        byte[] salt = PBKDF2.generateSalt();
        byte[] hashedPassword = PBKDF2.hashPassword(password, salt, 10000, 256);
        String hashedPasswordString = bytesToHex(hashedPassword);

        User mockUser = new User(1, username, hashedPasswordString, salt);
        when(userRepositoryMock.findByUsername(username)).thenReturn(mockUser);

        String token = userService.login(username, password);
        System.out.println(token);


        // Que o string “Bearer” existe no início da string - 1)
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
        String jwt = token.substring(7);
        //authMiddleware.validateTokenAndGetUserId(token)
        assertNotEquals(-1, authMiddleware.validateTokenAndGetUserId(jwt));

    }
}

=======
import cncs.academy.ess.service.TodoUserService;
import cncs.academy.ess.controller.AuthorizationMiddleware;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.cryptography.PBKDF2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static cncs.academy.ess.cryptography.PBKDF2.bytesToHex;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoUserServiceTest {

    private TodoUserService userService;
    private UserRepository userRepositoryMock;
    private AuthorizationMiddleware authMiddleware;

    //Executar antes de cada teste
    @BeforeEach
    void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        userService = new TodoUserService(userRepositoryMock);
        authMiddleware = new AuthorizationMiddleware(userRepositoryMock);
    }

    @Test
    void login_shouldReturnValidJWTTokenWhenCredentialsMatch() throws Exception {
        // Arrange
        String username = "alice";
        String password = "password123";

        // Gerar salt e hash para simular user real
        byte[] salt = PBKDF2.generateSalt();
        byte[] hashedPassword = PBKDF2.hashPassword(password, salt, 10000, 256);
        String hashedPasswordString = bytesToHex(hashedPassword);

        User mockUser = new User(1, username, hashedPasswordString, salt);
        when(userRepositoryMock.findByUsername(username)).thenReturn(mockUser);

        String token = userService.login(username, password);
        System.out.println(token);


        // Que o string “Bearer” existe no início da string - 1)
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
        String jwt = token.substring(7);
        //authMiddleware.validateTokenAndGetUserId(token)
        assertNotEquals(-1, authMiddleware.validateTokenAndGetUserId(jwt));

    }
}

>>>>>>> 773e6a9ac6b1c71abe68ca95199be9dfbf60ab74
