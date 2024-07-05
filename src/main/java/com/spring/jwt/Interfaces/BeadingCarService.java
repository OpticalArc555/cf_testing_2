package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.BeadingCAR.BeadingCARDto;
import com.spring.jwt.dto.BidCarsDTO;
import com.spring.jwt.dto.CarDto;
import com.spring.jwt.dto.FilterDto;
import com.spring.jwt.entity.Status;

import java.util.List;

public interface BeadingCarService {

    public String AddBCar(BeadingCARDto beadingCARDto);

    public String editCarDetails(BeadingCARDto beadingCARDto, Integer beadingCarId);

    public List<BeadingCARDto> getAllBeadingCars();

    public String deleteBCar(Integer beadingCarId);

   public BeadingCARDto getBCarById(Integer beadingCarId);

   public List<BeadingCARDto>getByUserId(int UserId);

   public List<BeadingCARDto> getByDealerID(Integer dealerId);

   public List<BeadingCARDto> getByStatus(String carStatus);


    Integer getCountByStatusAndDealerId(String carStatus, Integer dealerId);

    public List<BidCarsDTO> getAllLiveCars();
}
