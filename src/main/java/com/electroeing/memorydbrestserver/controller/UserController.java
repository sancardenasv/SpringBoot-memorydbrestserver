package com.electroeing.memorydbrestserver.controller;

import com.electroeing.memorydbrestserver.entities.Token;
import com.electroeing.memorydbrestserver.entities.User;
import com.electroeing.memorydbrestserver.repository.TokenRepository;
import com.electroeing.memorydbrestserver.repository.UserRepository;
import com.electroeing.memorydbrestserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${application.apiKey}")
    private String apiKey;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserController(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/signupNewUser")
    public Response signupNewUser(@RequestParam(name = "key") String apiKey, @RequestBody User user, HttpServletResponse httpServletResponse) {
        logger.info("Creating user - [{}][API KEY={}]", user, apiKey);
        try {
            if (this.apiKey.equals(apiKey)) {
                // Validate user does not exist
                final User existingUser = userRepository.findByEmail(user.getEmail());
                if (existingUser != null) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return new Response(false, "User already exists");
                }
                // Create user with md5 password
                user.setId(null);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(user.getPassword().getBytes());
                String hash = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
                user.setPassword(hash);
                final User newUser = userRepository.save(user);
                // Create token
                final Token token = new Token();
                token.setUserId(newUser.getId());
                token.setTokenString(UUID.randomUUID().toString());
                Calendar expirationDate = Calendar.getInstance();
                expirationDate.add(Calendar.MINUTE, 1);
                token.setExpirationDate(expirationDate.getTime());
                tokenRepository.save(token);
                // Return
                final Response response = new Response(true, "User created");
                response.addBodyObject("userId", newUser.getId());
                response.addBodyObject("idToken", token.getTokenString());
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                response.addBodyObject("expiresIn", sdf.format(token.getExpirationDate()));
                return response;
            }
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new Response(false, "Error creating user");

        } catch (NoSuchAlgorithmException e) {
            logger.error("Error saving new user", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response(false, "Error in service while creating user");
        }
    }

    @PostMapping("/verifyPassword")
    public Response verifyPassword(@RequestParam(name = "key") String apiKey, @RequestBody User user, HttpServletResponse httpServletResponse) {
        logger.info("Validating user - [{}][API KEY={}]", user, apiKey);
        try {
            if (this.apiKey.equals(apiKey)) {
                final User dbUser = userRepository.findByEmail(user.getEmail());
                // User exists
                if (dbUser == null) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return new Response(false, "Invalid User/Password");
                }
                // Password matches
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(user.getPassword().getBytes());
                String hash = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
                if (dbUser.getPassword().equals(hash)) {
                    // Validate Token
                    Token token = tokenRepository.findByUserId(dbUser.getId());
                    if (token == null) {
                        token = new Token();
                        token.setUserId(dbUser.getId());
                        token.setTokenString(UUID.randomUUID().toString());
                    }
                    Calendar expirationDate = Calendar.getInstance();
                    expirationDate.add(Calendar.MINUTE, 1);
                    token.setExpirationDate(expirationDate.getTime());
                    token = tokenRepository.save(token);
                    // Return
                    final Response response = new Response(true, "User authenticated");
                    response.addBodyObject("userId", dbUser.getId());
                    response.addBodyObject("idToken", token.getTokenString());
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                    response.addBodyObject("expiresIn", sdf.format(token.getExpirationDate()));
                    return response;
                }
            }
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new Response(false, "Invalid User/Password");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error saving new user", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response(false, "Error in service while validating user");
        }
    }
}
