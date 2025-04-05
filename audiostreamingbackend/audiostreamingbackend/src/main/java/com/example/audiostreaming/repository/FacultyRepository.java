// FacultyRepository.java
package com.example.audiostreaming.repository;

import com.example.audiostreaming.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}
