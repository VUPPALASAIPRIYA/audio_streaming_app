package com.example.audiostreaming.dto;

import lombok.Data;

@Data
public class AudioDTO {
    private Long id;
    private String facultyId;
    private byte[] data;
    private String audioUrl;
}