package com.example.audiostreaming.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String facultyId;
    private String hostelName;
    private LocalDateTime timestamp;
    @Lob
    private byte[] data;
    private String filename;
}