package com.spring.jwt.controller;


import com.spring.jwt.Interfaces.ICarRegister;
import com.spring.jwt.dto.*;
import com.spring.jwt.entity.Status;
import com.spring.jwt.exception.CarNotFoundException;
import com.spring.jwt.exception.DealerNotFoundException;
import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.repository.CarRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/car")
public class
CarController {

    private final ICarRegister iCarRegister;

    private final CarRepo carRepo;

    @PostMapping(value = "/carregister")
    public ResponseEntity<ResponseDto> carRegistration(@RequestBody CarDto carDto) {
        try{
            String result = iCarRegister.AddCarDetails(carDto);

            return (ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("success",result)));

        }catch (CarNotFoundException carNotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("unsuccess","Dealer not found"));
        }
//        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ResponseDto> carEdit(@RequestBody CarDto carDto, @PathVariable int id) {
        try {

            String result = iCarRegister.editCarDetails(carDto, id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("success",result));

        }catch (CarNotFoundException carNotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("unsuccess","car not found"));
        }

    }
    @GetMapping("/getAllCars")
    public ResponseEntity<?> getAllCars(@RequestParam int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<CarDto> listOfCar = iCarRegister.getAllCarsWithPages(pageNo, pageSize);
            int totalPages = getTotalPages(pageSize);

            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("success");
            responseAllCarDto.setList(listOfCar);

            return ResponseEntity.status(HttpStatus.OK).body(responseAllCarDto);
        } catch (CarNotFoundException carNotFoundException) {
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("unsuccessful");
            responseAllCarDto.setException("Car not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllCarDto);
        } catch (PageNotFoundException pageNotFoundException) {
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("unsuccessful");
            responseAllCarDto.setException("Page not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllCarDto);
        }
    }

    private int getTotalPages(int pageSize) {
        int totalCars = carRepo.getPendingAndActivateCarOrderedByIdDesc().size();
        return (int) Math.ceil((double) totalCars / pageSize);
    }



    @DeleteMapping("/removeCar")
    public ResponseEntity<ResponseDto> deleteCar(@RequestParam int carId, @RequestParam int dealerId){
        try {

            String result =iCarRegister.deleteCar(carId,dealerId);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("success",result));
        }
        catch (CarNotFoundException carNotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("unsuccess","car not found"));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("unsuccess",e.getMessage()));

        }
    }

    @GetMapping("/getCar")
    public ResponseEntity<ResponseSingleCarDto> findByArea(@RequestParam int car_id) {
        try {
            ResponseSingleCarDto responseSingleCarDto = new ResponseSingleCarDto("success");
            CarDto car = iCarRegister.findById(car_id);
            responseSingleCarDto.setObject(car);
            return ResponseEntity.status(HttpStatus.OK).body(responseSingleCarDto);
        }catch (CarNotFoundException carNotFoundException){
            ResponseSingleCarDto responseSingleCarDto = new ResponseSingleCarDto("unsuccess");
            responseSingleCarDto.setException("car not found by car id");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseSingleCarDto);
        }

//        return ResponseEntity.ok(cars.get());*
    }
    @GetMapping("/mainFilter/{pageNo}")
    public ResponseEntity<ResponseAllCarDto> searchByFilter(@RequestBody FilterDto filterDto, @PathVariable int pageNo){
        try{

            List<CarDto> listOfCar= iCarRegister.searchByFilter(filterDto,pageNo);
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("success");
            responseAllCarDto.setList(listOfCar);
            return ResponseEntity.status(HttpStatus.OK).body(responseAllCarDto);

        }
        catch (PageNotFoundException pageNotFoundException){
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("unsuccess");
            responseAllCarDto.setException("page not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllCarDto);
        }
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','DEALER')")
    @GetMapping("/dealer")
    public ResponseEntity<ResponseAllCarDto> getDetails(
            @RequestParam Integer dealerId,
            @RequestParam String carStatus,
            @RequestParam int pageNo) {

        try {
            Status status = Status.fromString(carStatus);
            List<CarDto> cars = iCarRegister.getDetails(dealerId, status, pageNo);
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("success");
            responseAllCarDto.setList(cars);
            return ResponseEntity.status(HttpStatus.OK).body(responseAllCarDto);

        } catch (CarNotFoundException carNotFoundException) {
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("unsuccess");
            responseAllCarDto.setException("car not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllCarDto);

        }catch (DealerNotFoundException dealerNotFoundException) {
            ResponseAllCarDto responseAllDealerDto = new ResponseAllCarDto("unsuccess");
            responseAllDealerDto.setException("Dealer not found by id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllDealerDto);
        } catch (PageNotFoundException pageNotFoundException) {
            ResponseAllCarDto responseAllCarDto = new ResponseAllCarDto("unsuccess");
            responseAllCarDto.setException("page not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseAllCarDto);
        }
    }

}



