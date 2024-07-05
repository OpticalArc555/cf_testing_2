package com.spring.jwt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class FinalBidDto {
    private Integer placed1stBidId;
    private Integer placedBidId;
    private Integer userId;
    private Integer bidCarId;
    private LocalDateTime dateTime;
    private Integer amount;


}
