package com.spring.jwt.dto.BeedingDtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PlacedBidDTO {
    private Integer placedBidId;
    private Integer userId;
    private Integer bidCarId;
    private LocalDateTime dateTime;
    private Integer amount;

}
