package com.poly.getrhymespresent2023.entities;

import com.poly.getrhymespresent2023.utils.State;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@RequiredArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "state", nullable = false)
    private State state;
}
