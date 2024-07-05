package com.spring.jwt.repository;

import com.spring.jwt.entity.BeadingCAR;
import com.spring.jwt.entity.BidCars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BidCarsRepo extends JpaRepository<BidCars, Integer> {

    @Query("SELECT b FROM BidCars b WHERE b.createdAt <= :currentTime AND b.closingTime >= :currentTime")
    List<BidCars> findAllLiveCars(LocalDateTime currentTime);
}
