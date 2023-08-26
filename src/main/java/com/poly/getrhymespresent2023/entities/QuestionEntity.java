package com.poly.getrhymespresent2023.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter
@Setter
public class QuestionEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer1", nullable = false)
    private String answer1;

    @Column(name = "answer2", nullable = false)
    private String answer2;

    @Column(name = "answer3", nullable = false)
    private String answer3;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "image_name")
    private String imageName;
}
