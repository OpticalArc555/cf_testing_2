package com.spring.jwt.controller;

import com.spring.jwt.Interfaces.PlacedBidService;
import com.spring.jwt.dto.BeedingDtos.PlacedBidDTO;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.exception.BidAmountLessException;
import com.spring.jwt.exception.BidForSelfAuctionException;
import com.spring.jwt.exception.InsufficientBalanceException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSocketBidController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketBidController.class);
    private final PlacedBidService placedBidService;
    private final SimpMessagingTemplate messagingTemplate;

    @PreAuthorize("permitAll")
    @MessageMapping("/placeBid")
    @SendTo("/topic/bids")
    public ResponseDto placeBid(PlacedBidDTO placedBidDTO) {
        try {
            logger.info("Received bid: {}", placedBidDTO);
            String result = placedBidService.placeBid(placedBidDTO, placedBidDTO.getBidCarId());

            messagingTemplate.convertAndSend("/topic/bids", placedBidDTO);
            return new ResponseDto("success", result);
        } catch (BidAmountLessException | UserNotFoundExceptions | BidForSelfAuctionException |
        InsufficientBalanceException e) {
            logger.error("Error placing bid: {}", e.getMessage());
            return new ResponseDto("error", e.getMessage());
        }
    }

    @PreAuthorize("permitAll")
    @MessageMapping("/topThreeBids")
    @SendTo("/topic/topThreeBids")
    public List<PlacedBidDTO> getTopThreeBids(PlacedBidDTO placedBidDTO) {
        try {
            if (placedBidDTO.getBidCarId() == null) {
                throw new IllegalArgumentException("Bid car ID must not be null");
            }
            return placedBidService.getTopThree(placedBidDTO.getBidCarId());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for top three bids: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
