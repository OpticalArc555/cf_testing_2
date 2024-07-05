package com.spring.jwt.repository;

import com.spring.jwt.dto.FinalBidDto;
import com.spring.jwt.entity.PlacedBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlacedBidRepo extends JpaRepository<PlacedBid, Integer> {

    List<PlacedBid> findByUserId(Integer userId);

    List<PlacedBid> findByBidCarId(Integer bidCarId);

    @Query("SELECT pb FROM PlacedBid pb WHERE pb.bidCarId = :bidCarId ORDER BY pb.amount DESC LIMIT 3")
    List<PlacedBid> findTop3ByBidCarIdOrderByAmountDesc(Integer bidCarId);

}
