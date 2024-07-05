package com.spring.jwt.service;

import com.spring.jwt.Interfaces.BeadingCarService;
import com.spring.jwt.dto.BeadingCAR.BeadingCARDto;
import com.spring.jwt.dto.BidCarsDTO;
import com.spring.jwt.entity.BeadingCAR;
import com.spring.jwt.entity.BidCars;
import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.BeadingCarNotFoundException;
import com.spring.jwt.exception.DealerNotFoundException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.BeadingCarRepo;
import com.spring.jwt.repository.BidCarsRepo;
import com.spring.jwt.repository.DealerRepository;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BeadingCarServiceImpl implements BeadingCarService {

    private final BeadingCarRepo beadingCarRepo;

    private final DealerRepository dealerRepository;

    private final UserRepository userRepository;

    private final BidCarsRepo bidCarsRepo;

    @Override
    public String AddBCar(BeadingCARDto beadingCARDto) {

        User byUserId = userRepository.findByUserId(beadingCARDto.getUserId());
        if(byUserId == null) {
            throw new UserNotFoundExceptions("User not found");
        }
        Set<Role> roles = byUserId.getRoles();
        System.err.println(roles);
        boolean isSalesPerson = roles.stream().anyMatch(role -> "INSPECTOR".equals(role.getName()));
        if(!isSalesPerson) {
            throw new RuntimeException("You're not authorized to perform this action");
        }

        if (beadingCARDto.getDealerId() == null) {
            throw new DealerNotFoundException("Dealer ID is required for adding a BeadingCAR");
        }
        if (!userRepository.existsById(beadingCARDto.getUserId())) {
            throw new UserNotFoundExceptions("User not found with ID: " + beadingCARDto.getUserId());
        }
        if (!dealerRepository.existsById(beadingCARDto.getDealerId())) {
            throw new DealerNotFoundException("Dealer not found with ID: " + beadingCARDto.getDealerId());
        }
        BeadingCAR beadingCAR = new BeadingCAR(beadingCARDto);
        beadingCAR.setCarStatus("pending");
        beadingCAR = beadingCarRepo.save(beadingCAR);
        return "BeadingCarId:" + beadingCAR.getBeadingCarId();
    }


    @Override
    public String editCarDetails(BeadingCARDto beadingCARDto, Integer beadingCarId) {
        BeadingCAR beadingCAR = beadingCarRepo.findById(beadingCarId)
                .orElseThrow(() -> new BeadingCarNotFoundException("beadingCAR not found", HttpStatus.NOT_FOUND));

        if (beadingCARDto.getAcFeature() != null) {
            beadingCAR.setAcFeature(beadingCARDto.getAcFeature());
        }
        if (beadingCARDto.getMusicFeature() != null) {
            beadingCAR.setMusicFeature(beadingCARDto.getMusicFeature());
        }
        if (beadingCARDto.getArea() != null) {
            beadingCAR.setArea(beadingCARDto.getArea());
        }
        if (beadingCARDto.getDate() != null) {
            beadingCAR.setDate(beadingCARDto.getDate());
        }
        if (beadingCARDto.getBrand() != null) {
            beadingCAR.setBrand(beadingCARDto.getBrand());
        }
        if (beadingCARDto.getCarInsurance() != null) {
            beadingCAR.setCarInsurance(beadingCARDto.getCarInsurance());
        }
        if (beadingCARDto.getCarStatus() != null) {
            beadingCAR.setCarStatus(beadingCARDto.getCarStatus());
        }
        if (beadingCARDto.getCity() != null) {
            beadingCAR.setCity(beadingCARDto.getCity());
        }
        if (beadingCARDto.getColor() != null) {
            beadingCAR.setColor(beadingCARDto.getColor());
        }
        if (beadingCARDto.getDescription() != null) {
            beadingCAR.setDescription(beadingCARDto.getDescription());
        }
        if (beadingCARDto.getFuelType() != null) {
            beadingCAR.setFuelType(beadingCARDto.getFuelType());
        }
        if (beadingCARDto.getKmDriven() != null) {
            beadingCAR.setKmDriven(beadingCARDto.getKmDriven());
        }
        if (beadingCARDto.getModel() != null) {
            beadingCAR.setModel(beadingCARDto.getModel());
        }
        if (beadingCARDto.getPowerWindowFeature() != null) {
            beadingCAR.setPowerWindowFeature(beadingCARDto.getPowerWindowFeature());
        }
        if (beadingCARDto.getOwnerSerial() != null) {
            beadingCAR.setOwnerSerial(beadingCARDto.getOwnerSerial());
        }
        if (beadingCARDto.getPrice() != null) {
            beadingCAR.setPrice(beadingCARDto.getPrice());
        }
        if (beadingCARDto.getRearParkingCameraFeature() != null) {
            beadingCAR.setRearParkingCameraFeature(beadingCARDto.getRearParkingCameraFeature());
        }
        if (beadingCARDto.getRegistration() != null) {
            beadingCAR.setRegistration(beadingCARDto.getRegistration());
        }
        if (beadingCARDto.getTransmission() != null) {
            beadingCAR.setTransmission(beadingCARDto.getTransmission());
        }
        if (beadingCARDto.getYear() != null) {
            beadingCAR.setYear(beadingCARDto.getYear());
        }
        if (beadingCARDto.getVariant() != null) {
            beadingCAR.setVariant(beadingCARDto.getVariant());
        }
        if (beadingCARDto.getTitle() != null) {
            beadingCAR.setTitle(beadingCARDto.getTitle());
        }
        if (beadingCARDto.getDealerId() != null) {
            beadingCAR.setDealerId(beadingCARDto.getDealerId());
        }

        beadingCarRepo.save(beadingCAR);
        return "beadingCAR edited";
    }


    @Override
    public List<BeadingCARDto> getAllBeadingCars() {
        List<BeadingCAR> beadingCars = beadingCarRepo.findAll();
        List<BeadingCARDto> dtos = new ArrayList<>();
        for (BeadingCAR beadingCAR : beadingCars) {
            dtos.add(new BeadingCARDto(beadingCAR));
        }
        return dtos;
    }

    @Override
    public String deleteBCar(Integer beadingCarId) {
        beadingCarRepo.deleteById(beadingCarId);
        return "Beading car deleted successfully";
    }

    @Override
    public BeadingCARDto getBCarById(Integer beadingCarId) {
        BeadingCAR beadingCAR = beadingCarRepo.findById(beadingCarId)
                .orElseThrow(() -> new BeadingCarNotFoundException("Beading car not found with id: " + beadingCarId, HttpStatus.NOT_FOUND));
        return new BeadingCARDto(beadingCAR);
    }

    @Override
    public List<BeadingCARDto> getByUserId(int UserId) {

        List<BeadingCAR> beadingCars = beadingCarRepo.findByUserId(UserId);
        if (beadingCars.isEmpty()) {
            throw new BeadingCarNotFoundException("No Beading cars found for user with id: " + UserId, HttpStatus.NOT_FOUND);
        }
        List<BeadingCARDto> dtos = new ArrayList<>();
        for (BeadingCAR beadingCAR : beadingCars) {
            dtos.add(new BeadingCARDto(beadingCAR));
        }
        return dtos;
    }

    @Override
    public List<BeadingCARDto> getByDealerID(Integer getDealerId) {
        List<BeadingCAR> beadingCars = beadingCarRepo.findByDealerId(getDealerId);
        if (beadingCars.isEmpty()) {
            throw new BeadingCarNotFoundException("No Beading cars found for dealer with id: " + getDealerId, HttpStatus.NOT_FOUND);
        }
        List<BeadingCARDto> dtos = new ArrayList<>();
        for (BeadingCAR beadingCAR : beadingCars) {
            dtos.add(new BeadingCARDto(beadingCAR));
        }
        return dtos;
    }

    @Override
    public List<BeadingCARDto> getByStatus(String carStatus) {
        List<BeadingCAR> beadingCars = beadingCarRepo.findByCarStatus(carStatus);
        if (beadingCars.isEmpty()) {
            throw new BeadingCarNotFoundException("No Beading cars found with status: " + carStatus, HttpStatus.NOT_FOUND);
        }
        List<BeadingCARDto> dtos = new ArrayList<>();
        for (BeadingCAR beadingCAR : beadingCars) {
            dtos.add(new BeadingCARDto(beadingCAR));
        }
        return dtos;

    }

    @Override
    public Integer getCountByStatusAndDealerId(String carStatus, Integer dealerId) {

        return beadingCarRepo.getCountByStatusAndDealerId(carStatus, dealerId);
    }

    @Override
    public List<BidCarsDTO> getAllLiveCars() {
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        List<BidCars> liveCars = bidCarsRepo.findAllLiveCars(currentTime);
        return liveCars.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BidCarsDTO convertToDto(BidCars beadingCar) {
        BidCarsDTO dto = new BidCarsDTO();
        dto.setBidCarId(beadingCar.getBidCarId());
        dto.setBeadingCarId(beadingCar.getBeadingCarId());
        dto.setCreatedAt(beadingCar.getCreatedAt());
        dto.setBasePrice(beadingCar.getBasePrice());
        dto.setUserId(beadingCar.getUserId());
        dto.setClosingTime(beadingCar.getClosingTime());
        return dto;
    }


}
