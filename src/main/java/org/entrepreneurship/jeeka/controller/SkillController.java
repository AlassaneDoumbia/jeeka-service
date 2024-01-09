package org.entrepreneurship.jeeka.controller;

import org.entrepreneurship.jeeka.entities.Skill;
import org.entrepreneurship.jeeka.entities.User;
import org.entrepreneurship.jeeka.helpers.ConstUtil;
import org.entrepreneurship.jeeka.helpers.LoggingUtils;
import org.entrepreneurship.jeeka.helpers.ResponseHelper;
import org.entrepreneurship.jeeka.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/entrepreneurship/api/skills")
public class SkillController {

    @Autowired
    SkillService skillService;

    @GetMapping("/get-all-skills")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_GESTIONNAIRE') or hasRole('ROLE_HELP')")
    public ResponseEntity allSkill() {
        List<Skill> skills = skillService.all();
        if (skills == null) {
            return ResponseHelper.returnError("Une erreur est survenue lors de la recuperation de la liste des competences");
        }
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }
    @GetMapping("/get-skill-info")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_GESTIONNAIRE') or hasRole('ROLE_HELP')")
    public ResponseEntity infoSkill(@RequestParam Long id) {
        Skill skills = skillService.info(id);
        if (skills == null) {
            return ResponseHelper.returnError("Une erreur est survenue lors de la recuperation des informations de la competence");
        }
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @RequestMapping(value = "/add-skill", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity addSkill(@RequestBody Map<String, String> params){

        try {
            String name = params.get("name");
            if (name.isEmpty()){
                return ResponseHelper.returnError("Une erreur est survenue, le libelle ne peut être vide ");
            }
            skillService.add(name);
            return ResponseHelper.returnSuccess("Compétence ajouté avec succès ");
        }catch (Exception e){
            LoggingUtils.error(e.getMessage());
            ConstUtil.catchException(e);
            return ResponseHelper.returnError("Une erreur interne est survenue, Merci de réessayer ");
        }

    }

    @RequestMapping(value = "/update-skill", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateSkill(@RequestBody Map<String, String> params){

        try {
            String id = params.get("id");
            String name = params.get("name");
            if (name.isEmpty()){
                return ResponseHelper.returnError("Une erreur est survenue, le libelle ne peut être vide ");
            }
            Skill skill = skillService.edit(Long.parseLong(id), name);
            if (skill == null){
                return ResponseHelper.returnError("Une erreur est survenue, lors de la mise à jour merci de réessayer");
            }
            return ResponseHelper.returnSuccess("Compétence modifié avec succès ");
        }catch (Exception e){
            LoggingUtils.error(e.getMessage());
            ConstUtil.catchException(e);
            return ResponseHelper.returnError("Une erreur interne est survenue, Merci de réessayer ");
        }
    }
}
