package com.poly.getrhymespresent2023.repositories;

import com.poly.getrhymespresent2023.entities.PresentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresentRepository extends JpaRepository<PresentEntity, Integer> {

    Optional<PresentEntity> findByUserId(int idUser);
}
