// Faculty.java
package com.example.audiostreaming.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Faculty {
    @Id
    private Long facultyId;
    private String name;
    private String password;
}