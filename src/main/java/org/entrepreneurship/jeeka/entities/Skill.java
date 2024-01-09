package org.entrepreneurship.jeeka.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "skill", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Skill extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    public Skill(String name) {
        this.name = name;
        this.setCreatedAt(new Date());
        this.setUpdatedAt(new Date());
    }
}
