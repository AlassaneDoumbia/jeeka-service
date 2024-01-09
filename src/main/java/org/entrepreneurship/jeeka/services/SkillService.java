package org.entrepreneurship.jeeka.services;

import org.entrepreneurship.jeeka.entities.Role;
import org.entrepreneurship.jeeka.entities.RoleName;
import org.entrepreneurship.jeeka.entities.Skill;
import org.entrepreneurship.jeeka.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    @Autowired
    SkillRepository skillRepository;

    public void add(String name){
        skillRepository.save(new Skill(name));}

    public Skill edit(Long id, String name){
        Skill skill = skillRepository.findById(id).orElse(null);
        if (skill == null){
            return skill;
        }
        skill.setName(name);
        return skillRepository.saveAndFlush(skill);
    }

    public List all(){return skillRepository.findAll();}

    public Skill info(Long id){return skillRepository.findById(id).orElse(null); }

    public Boolean existsByName(String name) {return skillRepository.existsByName(name);}

    public Optional<Skill> getSkillByName(String roleName) {
        return skillRepository.findByName(roleName);
    }
}
