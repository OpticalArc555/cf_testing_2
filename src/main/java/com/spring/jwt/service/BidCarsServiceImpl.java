package com.spring.jwt.service;

import com.spring.jwt.Interfaces.BidCarsService;
import com.spring.jwt.dto.BeadingCAR.BeadingCARDto;
import com.spring.jwt.dto.BidCarsDTO;
import com.spring.jwt.dto.BidDetailsDTO;
import com.spring.jwt.dto.ResDtos;
import com.spring.jwt.entity.*;
import com.spring.jwt.exception.BeadingCarNotFoundException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.BeadingCarRepo;
import com.spring.jwt.repository.BidCarsRepo;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BidCarsServiceImpl implements BidCarsService {

    private final ModelMapper modelMapper;

    private final BeadingCarRepo beadingCarRepo;

    private final BidCarsRepo bidCarsRepo;

    private final UserRepository userRepository;

    @Override
    public BidCarsDTO createBidding(BidCarsDTO bidCarsDTO) {

        User byUserId = userRepository.findByUserId(bidCarsDTO.getUserId());

        if(byUserId == null) {
            throw new UserNotFoundExceptions("User not found");
        }
        Set<Role> roles = byUserId.getRoles();
        System.err.println(roles);
        boolean isSalesPerson = roles.stream().anyMatch(role -> "SALESPERSON".equals(role.getName()));
        if(!isSalesPerson) {
            throw new RuntimeException("You're not authorized to perform this action");
        }
        Optional<BeadingCAR> byId = beadingCarRepo.findById(bidCarsDTO.getBeadingCarId());
        if (byId.isEmpty()) {
            throw new RuntimeException("Car Not Found");
        }

        BeadingCAR beadingCAR = byId.get();
        String carStatus = beadingCAR.getCarStatus();
        System.err.println("Car Status: " + carStatus);
        if (!"ACTIVE".equals(carStatus)) {
            throw new RuntimeException("Car is not Verified by SalesInspector, it can't be bid on.");
        }
        BidCars bidCars = convertToEntity(bidCarsDTO);
        BidCars save = bidCarsRepo.save(bidCars);
        return convertToDto(save);
    }

    @Override
    public BidDetailsDTO getbyBidId(Integer bidCarId, Integer beadingCarId) {
        Optional<BidCars> bidCarOptional  = bidCarsRepo.findById(bidCarId);
        Optional<BeadingCAR> beadingCarOptional  = beadingCarRepo.findById(beadingCarId);

        if (bidCarOptional.isPresent() && beadingCarOptional.isPresent()) {
            BidCars bidCar = bidCarOptional.get();
            BeadingCAR beadingCar = beadingCarOptional.get();

            BidDetailsDTO bidDetailsDTO = new BidDetailsDTO();

            bidDetailsDTO.setBidCarId(bidCar.getBidCarId());
            bidDetailsDTO.setBeadingCarId(beadingCar.getBeadingCarId());
            bidDetailsDTO.setClosingTime(bidCar.getClosingTime());
            bidDetailsDTO.setCreatedAt(bidCar.getCreatedAt());
            bidDetailsDTO.setMusicFeature(beadingCar.getMusicFeature());
            bidDetailsDTO.setArea(beadingCar.getArea());
            bidDetailsDTO.setBrand(beadingCar.getBrand());
            bidDetailsDTO.setCarInsurance(beadingCar.getCarInsurance());
            bidDetailsDTO.setCarStatus(beadingCar.getCarStatus());
            bidDetailsDTO.setCity(beadingCar.getCity());
            bidDetailsDTO.setColor(beadingCar.getColor());
            bidDetailsDTO.setDescription(beadingCar.getDescription());
            bidDetailsDTO.setFuelType(beadingCar.getFuelType());
            bidDetailsDTO.setKmDriven(beadingCar.getKmDriven());
            bidDetailsDTO.setModel(beadingCar.getModel());
            bidDetailsDTO.setOwnerSerial(beadingCar.getOwnerSerial());
            bidDetailsDTO.setPowerWindowFeature(beadingCar.getPowerWindowFeature());
            bidDetailsDTO.setPrice(beadingCar.getPrice());
            bidDetailsDTO.setRearParkingCameraFeature(beadingCar.getRearParkingCameraFeature());
            bidDetailsDTO.setRegistration(beadingCar.getRegistration());
            bidDetailsDTO.setTransmission(beadingCar.getTransmission());
            bidDetailsDTO.setYear(beadingCar.getYear());
            bidDetailsDTO.setDate(beadingCar.getDate());
            bidDetailsDTO.setUserId(beadingCar.getUserId());
            bidDetailsDTO.setVariant(beadingCar.getVariant());
            bidDetailsDTO.setTitle(beadingCar.getTitle());
            bidDetailsDTO.setDealer_id(beadingCar.getDealerId());

            return bidDetailsDTO;
        }else {
            throw new RuntimeException("Bid car or Beading car not found");
        }
    }

    @Override
    public List<BidCarsDTO> getByUserId(Integer userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundExceptions("User with ID: " + userId + " not found", HttpStatus.NOT_FOUND);
        }

        List<BeadingCAR> beadingCars = beadingCarRepo.findByUserId(userId);
        if (beadingCars.isEmpty()) {
            throw new BeadingCarNotFoundException("No Beading cars found for user with ID: " + userId, HttpStatus.NOT_FOUND);
        }

        List<BidCarsDTO> dtos = new ArrayList<>();
        for (BeadingCAR beadingCAR : beadingCars) {
            dtos.add(new BidCarsDTO(beadingCAR));
        }
        return dtos;
    }



    public BidCars convertToEntity(BidCarsDTO bidCarsDTO){
        BidCars bdCarEntity = modelMapper.map(bidCarsDTO, BidCars.class);
        return bdCarEntity;
    }

    public BidCarsDTO convertToDto (BidCars bidCars){
        BidCarsDTO bdCarDto = modelMapper.map(bidCars, BidCarsDTO.class);
        return bdCarDto;
    }
}
