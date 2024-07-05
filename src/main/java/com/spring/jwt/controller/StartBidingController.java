package com.spring.jwt.controller;

import com.spring.jwt.Interfaces.BidCarsService;
import com.spring.jwt.Interfaces.BiddingTimerService;
import com.spring.jwt.dto.*;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.BeadingCarNotFoundException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/Bidding/v1")
@RequiredArgsConstructor
public class StartBidingController {

    private final BiddingTimerService biddingTimerService;

    private final UserRepository userRepository;

    private final BidCarsService bidCarsService;

    private final JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(StartBidingController.class);

    @PostMapping("/SetTime")
    public ResponseEntity<?> setTimer(@RequestBody BiddingTimerRequestDTO biddingTimerRequest) {
        Optional<User> user = userRepository.findById(biddingTimerRequest.getUserId());
        if(!user.isPresent()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
        try {
            int durationMinutes = biddingTimerRequest.getDurationMinutes();
            startCountdown(durationMinutes);

            BiddingTimerRequestDTO biddingTimerRequestDTO = biddingTimerService.startTimer(biddingTimerRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ResDtos("success", biddingTimerRequestDTO));
        } catch (Exception e) {
            ResponseSingleCarDto responseSingleCarDto = new ResponseSingleCarDto("unsuccess");
            responseSingleCarDto.setException(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    private void startCountdown(int durationMinutes) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            pushNotificationToAllUsers();
        }, durationMinutes, TimeUnit.MINUTES);
    }
//    private void sendBulkNotification() {
//        try {
//            List<String> recipients = jdbcTemplate.queryForList("CALL send_emails()", String.class);
//            logger.info("Bulk notification sent successfully to recipients: " + recipients);
//        } catch (Exception e) {
//            logger.error("Failed to send bulk notification.", e);
//        }
//    }

    private void pushNotificationToAllUsers() {

        List<User> allUsers = userRepository.findAll();
        List<String> dealerEmails = allUsers.stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("DEALER")))
                .map(User::getEmail)
                .collect(Collectors.toList());

        try {
            sendNotification(dealerEmails, "Hurry Up Bidding is Started!");
            logger.info("Notification sent to users: " + dealerEmails);
        } catch (Exception e) {
            logger.error("Failed to send notification to users: " + dealerEmails, e);
        }
    }
    private void sendNotification(List<String> recipients, String message) {
        biddingTimerService.sendBulkEmails(recipients, message);
    }

    @PostMapping("/CreateBidding")
    public ResponseEntity<?> createBidding(@RequestBody BidCarsDTO bidCarsDTO) {
        try {
            BidCarsDTO bidding = bidCarsService.createBidding(bidCarsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new ResDtos("success", bidding));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<ResponseSingleCarDto> handleException(Exception e) {
        ResponseSingleCarDto responseSingleCarDto = new ResponseSingleCarDto("unsuccess");
        responseSingleCarDto.setException(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseSingleCarDto);
    }

    @GetMapping("/getById")
     public ResponseEntity<?> getbiddingcar (@RequestParam Integer bidCarId,@RequestParam  Integer beadingCarId) {
         BidDetailsDTO bidDetailsDTO = bidCarsService.getbyBidId(bidCarId, beadingCarId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResDtos("Success", bidDetailsDTO));
     }


    @GetMapping("/beadCarByUserId")
    public ResponseEntity<ResponseAllBidCarsDTO> getByUserId(@RequestParam Integer userId) {
        ResponseAllBidCarsDTO response = new ResponseAllBidCarsDTO("success");
        try {
            List<BidCarsDTO> bidCarsDTOs = bidCarsService.getByUserId(userId);
            response.setBookings(bidCarsDTOs);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundExceptions ex) {
            response.setStatus("error");
            response.setMessage(ex.getMessage());
            response.setException(ex.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (BeadingCarNotFoundException ex) {
            response.setStatus("error");
            response.setMessage(ex.getMessage());
            response.setException(ex.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}