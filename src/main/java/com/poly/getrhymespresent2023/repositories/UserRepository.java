package com.poly.getrhymespresent2023.repositories;

import com.poly.getrhymespresent2023.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByTelegramId(long telegramId);
}
