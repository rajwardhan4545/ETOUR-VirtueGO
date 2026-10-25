package com.example.services;

import com.example.dto.CustomerDTO;
import com.example.dto.LoginDTO;
import com.example.entities.CustomerMaster;
import com.example.enums.AuthProvider;
import com.example.enums.CustomerRole;
import com.example.mapper.CustomerMapper;
import com.example.model.CustomerModel;
import com.example.repositories.CustomerRepository;
import com.example.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private CustomerMaster customerMaster;
    private CustomerDTO customerDTO;
    private CustomerModel customerModel;

    @BeforeEach
    void setUp() {
        customerMaster = new CustomerMaster();
        customerMaster.setId(1);
        customerMaster.setEmail("john@example.com");
        customerMaster.setPassword("hashedPassword123");
        customerMaster.setCustomerRole(CustomerRole.CUSTOMER);
        customerMaster.setAuthProvider(AuthProvider.LOCAL);

        customerDTO = new CustomerDTO();
        customerDTO.setFirstName("John");
        customerDTO.setLastName("Doe");
        customerDTO.setEmail("john@example.com");
        customerDTO.setPassword("plainPassword123");

        customerModel = new CustomerModel();
        customerModel.setId(1);
        customerModel.setEmail("john@example.com");
    }

    @Test
    @DisplayName("register: Success path creates and returns user model")
    void testRegister_Success() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword123")).thenReturn("hashedPassword123");
        when(repository.save(any(CustomerMaster.class))).thenReturn(customerMaster);
        when(mapper.toModel(customerMaster)).thenReturn(customerModel);

        CustomerModel result = authService.register(customerDTO);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        assertNull(result.getPassword());
        verify(repository, times(1)).save(any(CustomerMaster.class));
    }

    @Test
    @DisplayName("register: Email already registered throws RuntimeException")
    void testRegister_EmailAlreadyExists_ThrowsException() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(customerMaster));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(customerDTO));
        assertEquals("Email already registered", ex.getMessage());
        verify(repository, never()).save(any(CustomerMaster.class));
    }

    @Test
    @DisplayName("login: Valid credentials return JWT token")
    void testLogin_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("john@example.com");
        loginDTO.setPassword("plainPassword123");

        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(customerMaster));
        when(passwordEncoder.matches("plainPassword123", "hashedPassword123")).thenReturn(true);
        when(jwtUtil.generateToken("john@example.com", "CUSTOMER")).thenReturn("mocked.jwt.token");

        String token = authService.login(loginDTO);

        assertNotNull(token);
        assertEquals("mocked.jwt.token", token);
    }

    @Test
    @DisplayName("login: Email not found throws RuntimeException")
    void testLogin_UserNotFound_ThrowsException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("unknown@example.com");
        loginDTO.setPassword("anyPassword");

        when(repository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginDTO));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    @DisplayName("login: Incorrect password throws RuntimeException")
    void testLogin_WrongPassword_ThrowsException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("john@example.com");
        loginDTO.setPassword("wrongPassword");

        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(customerMaster));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword123")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(loginDTO));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    @DisplayName("sendResetToken: User not found throws RuntimeException")
    void testSendResetToken_UserNotFound_ThrowsException() {
        when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.sendResetToken("missing@example.com"));
        assertEquals("User not found", ex.getMessage());
    }
}
