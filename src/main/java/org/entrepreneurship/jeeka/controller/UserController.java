package org.entrepreneurship.jeeka.controller;

import lombok.Data;
import org.entrepreneurship.jeeka.entities.Skill;
import org.entrepreneurship.jeeka.entities.User;
import org.entrepreneurship.jeeka.helpers.LoggingUtils;
import org.entrepreneurship.jeeka.helpers.ResponseHelper;
import org.entrepreneurship.jeeka.helpers.ResponseMessage;
import org.entrepreneurship.jeeka.services.SkillService;
import org.entrepreneurship.jeeka.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/entrepreneurship/api/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    SkillService skillService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/get-all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_GESTIONNAIRE') or hasRole('ROLE_HELP')")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userService.getAllUser();
        if (users == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/get-user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Optional<User>> getUserByUsername(@RequestParam(required = true) String username) {

        Optional<User> user = userService.getUserByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/update-user-status", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> userStatus(@Valid @RequestParam String status,
                                                      @RequestParam(required = true) String username) throws ParseException {

        ResponseMessage success = new ResponseMessage();

        try {
            success.setTimestamp(LocalDateTime.now());
            User currentUser = userService.getUserByUsername(username).orElse(null);
            if (currentUser == null){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            currentUser.setEnabled("0".equalsIgnoreCase(status) ? false : true);
            userService.addUser(currentUser);

            success.setMessage("L'utilisateur a été mise à jour avec succèss");
            success.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            success.setMessage(e.getMessage());
            success.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return new ResponseEntity<>(success, HttpStatus.CREATED);
    }

    @PostMapping("/update-user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> updateUser(@Valid @RequestBody saveUserForm userRequest) {
        LoggingUtils.info("updating user info :::::::: " + userRequest.getId());
        LoggingUtils.info("updating user new values :::::::: ");
        LoggingUtils.info("updating email :::::::: " + userRequest.getEmail());
        LoggingUtils.info("updating fistname :::::::: " + userRequest.getFirstname());
        LoggingUtils.info("updating lastname :::::::: " + userRequest.getLastname());

        ResponseMessage success = new ResponseMessage();
        try {

            success.setTimestamp(LocalDateTime.now());
            User user = userService.getUserByUsername(userRequest.getId()).orElse(null);
            if(user == null){
                success.setMessage("Utilisateur non trouvé, l'opération est annulé");
                success.setStatus(HttpStatus.OK.value());

                return new ResponseEntity<>(success, HttpStatus.NOT_FOUND);
            }

            user.setEmail(userRequest.getEmail());
            user.setPhone(userRequest.getPhone());
            user.setFirstname(userRequest.getFirstname());
            user.setLastname(userRequest.getLastname());
            userService.addUser(user);


            success.setMessage("L'utilisateur a été mis à jour");
            success.setStatus(HttpStatus.OK.value());

            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (Exception e) {
            success.setMessage(e.getMessage());
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/reset-forgoten-password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HELP')")
    public ResponseEntity<?> resetForgotenPassword(@RequestParam String username) {

        User user = userService.getUserByUsername(username).orElse(null);
        ResponseMessage success = new ResponseMessage();
        success.setTimestamp(LocalDateTime.now());

        if (user == null) {
            success.setMessage("Utilisateur non trouvé, l'opération est annulé");
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.NOT_FOUND);
        }

        LoggingUtils.info("Reinitialisation du mot de passe ::::: " + user.getUsername());

        char PasswordSet[] = {
                'a', 'Z', 'b', 'Y', 'c', '1', 'X', 'd', 'W', 'e', 'V', '2', 'f', 'U', 'g',
                'T', 'h', '3', 'S', 'i', 'R', 'j', 'Q', '4', 'k', 'P', 'l', 'O', 'm', '5',
                'N', 'n', 'M', 'o', 'L', '6', 'p', 'K', 'q', 'J', 'r', '7', 'I', 's', 'H',
                't', 'G', '8', 'u', 'F', 'v', 'E', 'w', '9', 'D', 'x', 'C', 'y', 'B', 'z', 'A'
        };

        String NewPassword = "";
        for (int i = 0; i < 16; i++) {

            Random random = new Random();
            int pick = random.nextInt(10);
            NewPassword += String.valueOf(PasswordSet[pick]);

        }
        LoggingUtils.info("New mot de passe ::::: " + NewPassword);
        user.setPassword(encoder.encode(NewPassword));
        user.setUpdatedAt(new Date());

        try {
            userService.addUser(user);

            success.setMessage(NewPassword);
            success.setStatus(HttpStatus.OK.value());

            return new ResponseEntity<>(success, HttpStatus.OK);

        } catch (Exception e) {

            success.setMessage(e.getMessage());
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordForm passwordForm) {
        User user = userService.getUserByUsername(passwordForm.getUsername()).orElse(null);
        ResponseMessage success = new ResponseMessage();
        success.setTimestamp(LocalDateTime.now());
        if (user == null) {
            success.setMessage("Utilisateur non trouvé, l'opération est annulé");
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.NOT_FOUND);
        }

        boolean result = passwordEncoder.matches(passwordForm.getOldpassword(), user.getPassword());
        LoggingUtils.info("check password result :::::: " + result);

        if (!result){
            success.setMessage("L'ancien mot de passe n'est pas conforme, l'opération est annulé");
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.NOT_FOUND);
        }

//        LoggingUtils.info("user passwordForm ::::: "+passwordForm.getPassword());
        LoggingUtils.info("Modification du mot de passe ::::: " + user.getUsername());
//        LoggingUtils.info("user passwordForm encode ::::: "+encoder.encode(passwordForm.getPassword()));
        user.setPassword(encoder.encode(passwordForm.getNewpassword()));
        user.setUpdatedAt(new Date());
        try {
            userService.addUser(user);

            success.setMessage("L'utilisateur a été mis à jour");
            success.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (Exception e) {
            success.setMessage(e.getMessage());
            success.setStatus(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(success, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/add-skill-user")
    public ResponseEntity addSkillUser(@RequestBody Map<String, String> params){

        String username = params.get("username");
        String skill = params.get("skill");
        User user = userService.getUserByUsername(username).orElse(null);
        Skill skillToAdd = skillService.getSkillByName(skill).orElse(null);

        LoggingUtils.info("username :::::::::: "+username);
        LoggingUtils.info("skill :::::::::: "+skill);
        LoggingUtils.info("user :::::::::: "+user);
        LoggingUtils.info("skillToAdd :::::::::: "+skillToAdd);
        LoggingUtils.info("skillToAdd :::::::::: "+(skillToAdd == null));

        if(user == null || skillToAdd == null){
            return ResponseHelper.returnError("Merci de vérifier les informations fournis");
        }

        LoggingUtils.info("continue :::::::::: ");
        List liste = user.getSkills();
        liste.add(skillToAdd);
        user.setSkills(liste);
        userService.update(user);
        return ResponseHelper.returnSuccess("La compétence "+skill+" est ajouté à l'utilisateur "+username);
    }
    @PostMapping("/remove-skill-user")
    public ResponseEntity removeSkillUser(@RequestBody Map<String, String> params){

        String username = params.get("username");
        String skill = params.get("skill");
        User user = userService.getUserByUsername(username).orElse(null);
        Skill skillToAdd = skillService.getSkillByName(skill).orElse(null);

        LoggingUtils.info("username :::::::::: "+username);
        LoggingUtils.info("skill :::::::::: "+skill);
        LoggingUtils.info("user :::::::::: "+user);
        LoggingUtils.info("skillToAdd :::::::::: "+skillToAdd);
        LoggingUtils.info("skillToAdd :::::::::: "+(skillToAdd == null));

        if(user == null || skillToAdd == null){
            return ResponseHelper.returnError("Merci de vérifier les informations fournis");
        }
        List<Skill> filteredList;
        LoggingUtils.info("continue :::::::::: ");
        List liste = user.getSkills();
        liste.add(skillToAdd);
        Predicate<Skill> byName = skills -> !skills.getName().equalsIgnoreCase(""+skill);

        filteredList = (List<Skill>) liste.stream().filter(byName)
                .collect(Collectors.toList());

        System.out.println(filteredList);

        user.setSkills(liste);
        userService.update(user);
        return ResponseHelper.returnSuccess("La compétence "+skill+" est ajouté à l'utilisateur "+username);
    }

    @Data
    public static class PasswordForm {
        @NotBlank
        private String username;
        @NotBlank
        private String oldpassword;
        @NotBlank
        private String newpassword;
    }
    @Data
    public static class saveUserForm {
        @NotBlank
        @Size(min=3, max = 60)
        private String id;

        @NotBlank
        @Size(min=3, max = 60)
        private String firstname;

        @NotBlank
        @Size(min=3, max = 60)
        private String lastname;

        @NotBlank
        @Size(min=3, max = 60)
        private String phone;

        @NotBlank
        @Size(min=3, max = 60)
        private String email;


    }
}
