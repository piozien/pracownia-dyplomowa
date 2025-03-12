package graduate.finance_dashboard.service;

import graduate.finance_dashboard.config.EmailValidator;
import graduate.finance_dashboard.dto.LoginRequest;
import graduate.finance_dashboard.dto.RegistrationRequest;
import graduate.finance_dashboard.exception.ApiException;
import graduate.finance_dashboard.model.User;
import graduate.finance_dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailValidator emailValidator;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found with username: " + username, HttpStatus.NOT_FOUND));
    }

    public User login(LoginRequest loginRequest) {
        User user = getUserByUsername(loginRequest.getUsername());
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid password", HttpStatus.UNAUTHORIZED);
        }
        return user;
    }

    public String register(RegistrationRequest request) {
        // Validate email
        if (!emailValidator.test(request.getEmail())) {
            throw new ApiException("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        // Check if email exists
        boolean emailExists = userRepository.findByEmail(request.getEmail()).isPresent();
        if (emailExists) {
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }

        // Check if username exists
        boolean usernameExists = userRepository.findByUsername(request.getUsername()).isPresent();
        if (usernameExists) {
            throw new ApiException("Username already exists", HttpStatus.CONFLICT);
        }

        // Create and save user
        User user = new User(
            request.getFirstName(),
            request.getLastName(),
            request.getUsername(),
            bCryptPasswordEncoder.encode(request.getPassword()),
            request.getEmail(),
            LocalDateTime.now()
        );

        userRepository.save(user);
        return "User registered successfully";
    }
}
