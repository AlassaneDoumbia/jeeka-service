package org.entrepreneurship.jeeka.controller;

import lombok.Data;
import lombok.ToString;
import org.entrepreneurship.jeeka.Security.jwt.JwtProvider;
import org.entrepreneurship.jeeka.entities.Role;
import org.entrepreneurship.jeeka.entities.RoleName;
import org.entrepreneurship.jeeka.entities.User;
import org.entrepreneurship.jeeka.helpers.JwtResponse;
import org.entrepreneurship.jeeka.helpers.LoggingUtils;
import org.entrepreneurship.jeeka.helpers.ResponseHelper;
import org.entrepreneurship.jeeka.helpers.ResponseMessage;
import org.entrepreneurship.jeeka.services.RoleService;
import org.entrepreneurship.jeeka.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/entrepreneurship/api")
public class IndexController {


    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @RequestMapping(path = {"", "/", "/home"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity index() {
        LoggingUtils.info("Home page");
        return ResponseHelper.returnSuccess("WELCOME TO JEEKA WEB SERVICES V1.0.0");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginForm loginRequest) {
        System.out.println(loginRequest.toString());
        System.out.println("login for user :::::: " + loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        System.out.println("login authentication :::::: " + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        System.out.println("login jwt :::::: " + jwt);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }


    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(@Valid @RequestBody RegisterForm registerRequest) {
        ResponseMessage success = new ResponseMessage();
        success.setTimestamp(LocalDateTime.now());
        LoggingUtils.info("Register for user :::::: " + registerRequest.getUsername());
        LoggingUtils.info(registerRequest.toString());
        String username = registerRequest.getUsername();
        LoggingUtils.info("Register username :::::: " + username);
        try {
            if (username.equalsIgnoreCase(registerRequest.getPassword())) {
                success.setMessage("Le nom d'utilisateur et le mot de passe ne peuvent pas être identitique !");
                success.setStatus(HttpStatus.CONFLICT.value());
                return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);
            }
            if (userService.existsByUsername(username)) {
                success.setMessage("Le nom d'utilisateur est déjà utilisé !");
                success.setStatus(HttpStatus.CONFLICT.value());
                return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);
            }
            if (registerRequest.getEmail() != null && userService.existsByEmail(registerRequest.getEmail())) {
                success.setMessage("L'adresse email existe déjà !");
                success.setStatus(HttpStatus.CONFLICT.value());
                return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);
            }
            // Creating user's account
            success.setMessage("Création du nouveau utilisateur en cours !!!!!!");
            User user = new User(username,
                    encoder.encode(registerRequest.getPassword()),
                    true
            );

            Set<String> strRoles = registerRequest.getRole();
            Set<Role> roles = new HashSet<>();

            strRoles.forEach(role -> {
                System.out.println("role ::::::: " + role);
                switch (role) {
                    case "admin":
                        Role adminRole = roleService.getRoleByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        System.out.println("adminRole ::::::: " + adminRole.getName());
                        roles.add(adminRole);

                        break;
                    case "gestion":
                        Role gestionRole = roleService.getRoleByName(RoleName.ROLE_GESTIONNAIRE)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        System.out.println("gestionRole ::::::: " + gestionRole.getName());
                        roles.add(gestionRole);

                        break;
                    case "help":
                        Role helpRole = roleService.getRoleByName(RoleName.ROLE_HELP)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        System.out.println("helpRole ::::::: " + helpRole.getName());
                        roles.add(helpRole);

                        break;
                    default:
                        Role userRole = roleService.getRoleByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        System.out.println("userRole ::::::: " + userRole.getName());
                        roles.add(userRole);
                }
            });
            // save user
            user.setRoles(roles);

            user.setFirstname(registerRequest.getFirstname());
            user.setLastname(registerRequest.getLastname());
            user.setPhone(username);
            user.setEmail(registerRequest.getEmail());
            user.setUpdatedAt(new Date());
            user.setCreatedAt(new Date());

            userService.addUser(user);

            // return request success
            success.setMessage("Merci de vous être inscrit.");
            success.setStatus(HttpStatus.OK.value());
            success.setData(user.getId());
            return new ResponseEntity<>(success, HttpStatus.OK);

        } catch (Exception e) {
            LoggingUtils.info("erreur ::::: " + e.getMessage());
            e.printStackTrace(System.out);
            e.printStackTrace(System.err);
            success.setMessage(e.getMessage());
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.NOT_FOUND);
        }

    }

    @Data
    @ToString
    public static class LoginForm {
        @NotBlank
        @Size(min=3, max = 60)
        private String username;

        @NotBlank
        @Size(min = 6, max = 40)
        private String password;
    }

    @Data
    @ToString
    public static class RegisterForm {

        @Size(min = 3, max = 50)
        private String firstname;

        @Size(min = 3, max = 50)
        private String lastname;

        @Size(min = 3, max = 50)
        private String phone;

        @Size(max = 60)
        @Email
        private String email;

        @NotBlank
        private Set<String> role;

        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Size(min = 6, max = 40)
        private String password;


    }
}
