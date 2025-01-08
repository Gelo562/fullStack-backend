package com.example.projback.controller;

import com.example.projback.dto.LoginRequestDTO;
import com.example.projback.dto.LoginResponseDTO;
import com.example.projback.dto.ResponseDTO;
import com.example.projback.entity.Role;
import com.example.projback.entity.User;
import com.example.projback.config.JwtUtil;
import com.example.projback.service.UserService;
import org.hibernate.mapping.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth/")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody User user){
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        try {
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.badRequest().body(
                        new ResponseDTO<>(false, "Invalid username or password", null)
                );
            }

            String token = jwtUtil.generateToken(user.getUsername());
            LoginResponseDTO responseDTO = new LoginResponseDTO(token, user.getRole());
            return ResponseEntity.ok(new ResponseDTO<>(true, "Login successful", responseDTO));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ResponseDTO<>(false, e.getMessage(), null)
            );
        }
    }

}

