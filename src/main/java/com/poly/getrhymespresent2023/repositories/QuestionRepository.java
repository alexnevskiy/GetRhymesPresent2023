package com.poly.getrhymespresent2023.repositories;

import com.poly.getrhymespresent2023.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {
}
