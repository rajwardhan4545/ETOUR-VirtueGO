package com.example.config;

import com.example.entities.CustomerMaster;
import com.example.enums.AuthProvider;
import com.example.enums.CustomerRole;
import com.example.repositories.CustomerRepository;
import com.example.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        CustomerMaster user = repository.findByEmail(email)
                .orElseGet(() -> {
                    CustomerMaster u = new CustomerMaster();
                    u.setEmail(email);
                    u.setFirstName(firstName);
                    u.setLastName(lastName);
                    u.setCustomerRole(CustomerRole.CUSTOMER);
                    u.setAuthProvider(AuthProvider.GOOGLE);
                    u.setProfileCompleted(false);
                    u.setPassword(null);
                    return repository.save(u);
                });

        String jwt = jwtUtil.generateToken(
                user.getEmail(),
                user.getCustomerRole().name()
        );

        // ONLY redirect - don't write to response
        response.sendRedirect(frontendUrl + "/oauth2-success?token=" + jwt);
    }
}