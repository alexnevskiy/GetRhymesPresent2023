package com.poly.getrhymespresent2023.repositories;

import com.poly.getrhymespresent2023.entities.TrustedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrustedUserRepository extends JpaRepository<TrustedUserEntity, Integer> {

    TrustedUserEntity findByTelegramId(long telegramId);
}
