// AudioRepository.java
package com.example.audiostreaming.repository;

import com.example.audiostreaming.entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioRepository extends JpaRepository<Audio, Long> {
}