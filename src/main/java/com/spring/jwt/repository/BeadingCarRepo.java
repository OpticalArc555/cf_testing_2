package com.spring.jwt.repository;

import com.spring.jwt.entity.BeadingCAR;
import com.spring.jwt.entity.CarVerified;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BeadingCarRepo extends JpaRepository<BeadingCAR, Integer>, JpaSpecificationExecutor<BeadingCAR> {

    List<BeadingCAR> findByUserId (int userId);

    List<BeadingCAR> findByDealerId(Integer getDealerId);


    List<BeadingCAR> findByCarStatus(String carStatus);
    @Query("SELECT COUNT(b) FROM BeadingCAR b WHERE b.carStatus = :carStatus AND b.dealerId = :dealerId")
    Integer getCountByStatusAndDealerId(@Param("carStatus") String carStatus, @Param("dealerId") Integer dealerId);


}
