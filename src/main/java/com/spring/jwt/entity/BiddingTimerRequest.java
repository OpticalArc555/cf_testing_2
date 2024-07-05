package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BiddingTimerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="BiddingTimerId")
    private Integer BiddingTimerId;

    @Column(name="beadingCarId")
    private Integer beadingCarId;

    @Column(name="userId")
    private Integer userId;

    @Column(name="basePrice")
    private Integer basePrice;

    @Column(name="durationMinutes")
    private int durationMinutes;

}
