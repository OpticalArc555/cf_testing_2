package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.CarDto;
import com.spring.jwt.dto.FilterDto;
import com.spring.jwt.entity.Status;

import java.util.List;

public interface ICarRegister {
    public String AddCarDetails(CarDto carDto);

    public String editCarDetails(CarDto carDto,int id);

    public List<CarDto> getAllCarsWithPages(int PageNo, int pageSize);

    public String deleteCar(int carId, int DealerId);

    CarDto getCarById(int carId);

//    public Optional<List<Car>> FindByArea(String area);

    public List<CarDto> searchByFilter(FilterDto filterDto, int pageNo);

    public CarDto findById(int carId);

    public String editCarDetails(CarDto carDto);

    public List<CarDto> getDetails(int dealerId, Status carStatus, int pageNo);
}
