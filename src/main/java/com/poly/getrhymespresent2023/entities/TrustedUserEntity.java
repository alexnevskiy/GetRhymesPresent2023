package com.poly.getrhymespresent2023.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trusted_user")
@RequiredArgsConstructor
@Getter
@Setter
public class TrustedUserEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @Column(name = "username", nullable = false)
    private String username;
}
