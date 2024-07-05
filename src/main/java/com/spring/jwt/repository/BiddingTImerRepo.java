package com.spring.jwt.repository;

import com.spring.jwt.entity.BiddingTimerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiddingTImerRepo extends JpaRepository<BiddingTimerRequest, Integer> {
}
