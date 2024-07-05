package com.spring.jwt.entity;

import com.spring.jwt.dto.BeadingCAR.BeadingCARDto;
import com.spring.jwt.dto.CarDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@AllArgsConstructor
@Table(name = "BeadingCAR")
public class BeadingCAR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beadingCarId", nullable = false)
    private Integer beadingCarId;

    @Column(name = "ac_feature")
    private Boolean acFeature;

    @Column(name = "music_feature")
    private Boolean musicFeature;

    @Column(name = "area", length = 45)
    private String area;

    @Column(name = "brand", nullable = false, length = 45)
    private String brand;

    @Column(name = "carStatus", length = 50)
    private String carStatus;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "color", length = 45)
    private String color;

    @Column(name = "description", length = 5000)
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @Column(name = "fuel_type", length = 45)
    private String fuelType;

    @Column(name = "km_driven", length = 50)
    private Integer kmDriven;

    @Column(name = "model", length = 45)
    private String model;

    @Column(name = "owner_serial")
    private Integer ownerSerial;

    @Column(name = "power_window_feature")
    private Boolean powerWindowFeature;

    @Column(name = "price", length = 45)
    private Integer price;

    @Column(name = "rear_parking_camera_feature")
    private Boolean rearParkingCameraFeature;

    @Column(name = "registration", length = 45)
    private String registration;

    @Column(name = "transmission", length = 45)
    private String transmission;

    @Column(name = "year")
    private Integer year;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "UserId")
    private int userId;

    @Column(name = "car_insurance")
    private Boolean carInsurance;

    @Column(name = "variant", length = 45)
    private String variant;

    @Column(name = "title", length = 250)
    private String title;

    @Column(name = "dealer_id")
    private Integer dealerId;




    public BeadingCAR() {
    }

    public BeadingCAR(BeadingCARDto beadingCARDto) {
        this.beadingCarId = beadingCARDto.getBeadingCarId();
        this.acFeature = beadingCARDto.getAcFeature();
        this.musicFeature = beadingCARDto.getMusicFeature();
        this.carInsurance = beadingCARDto.getCarInsurance();
        this.area = beadingCARDto.getArea();
        this.brand = beadingCARDto.getBrand();
        this.carStatus = beadingCARDto.getCarStatus() != null ? beadingCARDto.getCarStatus() : "pending";
        this.city = beadingCARDto.getCity();
        this.color = beadingCARDto.getColor();
        this.description = beadingCARDto.getDescription();
        this.fuelType = beadingCARDto.getFuelType();
        this.kmDriven = beadingCARDto.getKmDriven();
        this.model = beadingCARDto.getModel();
        this.ownerSerial = beadingCARDto.getOwnerSerial();
        this.powerWindowFeature = beadingCARDto.getPowerWindowFeature();
        this.price = beadingCARDto.getPrice();
        this.rearParkingCameraFeature = beadingCARDto.getRearParkingCameraFeature();
        this.registration = beadingCARDto.getRegistration();
        this.transmission = beadingCARDto.getTransmission();
        this.year = beadingCARDto.getYear();
        this.date = beadingCARDto.getDate();
        this.userId = beadingCARDto.getUserId();
        this.title= beadingCARDto.getTitle();
        this.variant=beadingCARDto.getVariant();
        this.dealerId= beadingCARDto.getDealerId();
    }


}