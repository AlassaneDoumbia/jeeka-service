package org.entrepreneurship.jeeka.services;

import org.entrepreneurship.jeeka.entities.Role;
import org.entrepreneurship.jeeka.entities.RoleName;
import org.entrepreneurship.jeeka.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }
}
