package com.poly.getrhymespresent2023.repositories;

import com.poly.getrhymespresent2023.entities.SessionEntity;
import com.poly.getrhymespresent2023.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionEntity, Integer> {

    List<SessionEntity> findByUser(UserEntity user);

    @Transactional
    long deleteByUser(UserEntity user);
}
