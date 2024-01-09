package org.entrepreneurship.jeeka.repositories;

import org.entrepreneurship.jeeka.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String name);
    Boolean existsByName(String name);
}
