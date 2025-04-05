package com.example.audiostreaming.controller;

import com.example.audiostreaming.entity.Audio;
import com.example.audiostreaming.entity.Student;
import com.example.audiostreaming.repository.AudioRepository;
import com.example.audiostreaming.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private AudioRepository audioRepository;

    @Autowired
    private StudentRepository studentRepository;

    private final String audioStorageDirectory = "audio-storage/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hostel") String hostel,
            @RequestParam("facultyId") String facultyId) {

        File webmFile = null;
        File mp3File = null;
        try {
            webmFile = File.createTempFile("audio", ".webm");
            file.transferTo(webmFile);

            mp3File = File.createTempFile("audio", ".mp3");

            AudioAttributes audioAttributes = new AudioAttributes();
            audioAttributes.setCodec("libmp3lame");
            audioAttributes.setBitRate(128000);
            audioAttributes.setChannels(1);
            audioAttributes.setSamplingRate(44100);

            EncodingAttributes encodingAttributes = new EncodingAttributes();
            encodingAttributes.setOutputFormat("mp3");
            encodingAttributes.setAudioAttributes(audioAttributes);

            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(webmFile), mp3File, encodingAttributes);

            String mp3FileName = System.currentTimeMillis() + ".mp3";
            Path mp3FilePath = Paths.get(audioStorageDirectory, mp3FileName);
            Files.createDirectories(Paths.get(audioStorageDirectory));
            Files.write(mp3FilePath, Files.readAllBytes(mp3File.toPath()));

            byte[] audioData;
            try {
                audioData = Files.readAllBytes(mp3File.toPath());
                System.out.println("Audio data size before save: " + audioData.length);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading audio file.");
            }

            if (hostel.equals("all")) {
                List<String> allHostelNames = studentRepository.findAll().stream()
                        .map(Student::getHostelName)
                        .distinct()
                        .collect(Collectors.toList());
                for (String hostelName : allHostelNames) {
                    Audio audio = new Audio();
                    audio.setFilename(mp3FileName);
                    audio.setHostelName(hostelName);
                    audio.setFacultyId(facultyId);
                    audio.setTimestamp(LocalDateTime.now());
                    audio.setData(audioData);
                    audioRepository.save(audio);
                    System.out.println("Audio saved for hostel: " + hostelName + " with ID: " + audio.getId());
                }
                return ResponseEntity.ok("Audio uploaded successfully to all hostels.");
            } else {
                Audio audio = new Audio();
                audio.setFilename(mp3FileName);
                audio.setHostelName(hostel);
                audio.setFacultyId(facultyId);
                audio.setTimestamp(LocalDateTime.now());
                audio.setData(audioData);
                audioRepository.save(audio);
                System.out.println("Audio saved for hostel: " + hostel + " with ID: " + audio.getId());
                return ResponseEntity.ok("Audio uploaded successfully to " + hostel);
            }

        } catch (IOException | EncoderException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload audio: " + e.getMessage());
        } finally {
            if (webmFile != null) {
                try {
                    Files.deleteIfExists(webmFile.toPath());
                } catch (IOException e) {
                    System.err.println("Error deleting temporary webm file: " + e.getMessage());
                }
            }
            if (mp3File != null) {
                try {
                    Files.deleteIfExists(mp3File.toPath());
                } catch (IOException e) {
                    System.err.println("Error deleting temporary mp3 file: " + e.getMessage());
                }
            }
        }
    }

    @GetMapping("/getForHostel/{hostel}")
    public ResponseEntity<List<Audio>> getAudioByHostel(@PathVariable String hostel) {
        List<Audio> audios = audioRepository.findAll().stream().filter(a -> a.getHostelName().equals(hostel)).toList();

        if (audios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        for (Audio audio : audios) {
            if (audio.getData() == null) {
                System.out.println("Audio data is null for audio ID: " + audio.getId());
            } else {
                System.out.println("Audio data size for audio ID: " + audio.getId() + " is : " + audio.getData().length);
            }
        }

        return ResponseEntity.ok(audios);
    }

    @GetMapping("/getStudent/{studentId}")
    public ResponseEntity<String> getStudentHostel(@PathVariable Long studentId) {
        Student student = studentRepository.findByStudentId(studentId).orElse(null);
        if (student != null) {
            return ResponseEntity.ok(student.getHostelName());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}