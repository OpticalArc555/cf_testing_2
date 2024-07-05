package com.spring.jwt.service;

import com.spring.jwt.Interfaces.PlacedBidService;
import com.spring.jwt.Wallet.Entity.WalletAccount;
import com.spring.jwt.Wallet.Repo.AccountRepository;
import com.spring.jwt.dto.BeedingDtos.PlacedBidDTO;
import com.spring.jwt.entity.BidCars;
import com.spring.jwt.entity.PlacedBid;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.*;
import com.spring.jwt.repository.BidCarsRepo;
import com.spring.jwt.repository.PlacedBidRepo;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlacedBidServiceImpl implements PlacedBidService {
    private final PlacedBidRepo placedBidRepo;

    private final  BidCarsRepo bidCarsRepo;

    private final ModelMapper modelMapper;

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;


    @Override
    public String placeBid(PlacedBidDTO placedBidDTO, Integer bidCarId) throws BidAmountLessException, BidForSelfAuctionException {
        Optional<BidCars> carbyId = bidCarsRepo.findById(bidCarId);

        User byUserId = userRepository.findByUserId(placedBidDTO.getUserId());

        if (byUserId== null) {
            throw new UserNotFoundExceptions("User Not Found By Id "+ placedBidDTO.getUserId());
        }

//        Optional<WalletAccount> accountbalance = accountRepository.findByUserId(placedBidDTO.getUserId());
//
//        if (accountbalance.isEmpty()) {
//            throw new UserNotFoundExceptions("Account balance not found for user: " + placedBidDTO.getUserId());
//        }
//        WalletAccount accountBalance = accountbalance.get();
//        if (accountBalance.getOpeningBalance() <= 2000) {
//            throw new InsufficientBalanceException("Minimum Balance for placing bid should be greater than 2000");
//        }

        if (carbyId.isEmpty()){
            throw new UserNotFoundExceptions("Bid Cannot Be Placed as Car is Not Found in Our Database");
        }
        BidCars bidCar = carbyId.get();

        if (bidCar.getUserId().equals(placedBidDTO.getUserId())) {
            throw new BidForSelfAuctionException("You cannot place a bid on your own car");
        }
        PlacedBid placedBid = convertToEntity(placedBidDTO);
        if(placedBid.getAmount()< carbyId.get().getBasePrice()){

            throw new BidAmountLessException("Bid amount cannot be less than base price");
        }

        placedBid.setBidCarId(bidCarId);
        placedBidRepo.save(placedBid);

        return "Bid Placed Successfully";
    }

    @Override
    public List<PlacedBidDTO> getByUserId(Integer userId) throws UserNotFoundExceptions {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundExceptions("User not found with ID: " + userId);
        }
        List<PlacedBid> bids = placedBidRepo.findByUserId(userId);
        return bids.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlacedBidDTO> getByCarID(Integer bidCarId) throws BidNotFoundExceptions {
        Optional<BidCars> bidCar = bidCarsRepo.findById(bidCarId);
        if (bidCar.isEmpty()) {
            throw new BidNotFoundExceptions("Bid car not found with ID: " + bidCarId);
        }
        List<PlacedBid> bids = placedBidRepo.findByBidCarId(bidCarId);
        return bids.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlacedBidDTO getById(Integer placedBidId) throws PlacedBidNotFoundExceptions {
        Optional<PlacedBid> optionalPlacedBid = placedBidRepo.findById(placedBidId);
        if (optionalPlacedBid.isPresent()) {
            PlacedBid placedBid = optionalPlacedBid.get();
            System.out.println(placedBid);
            return convertToDto(placedBid);
        } else {
            throw new PlacedBidNotFoundExceptions("PlacedBid not found with ID: " + placedBidId);
        }
    }

    @Override
    public List<PlacedBidDTO> getTopThree(Integer bidCarId) throws BidNotFoundExceptions {
        Optional<BidCars> bidCar = bidCarsRepo.findById(bidCarId);
        if (bidCar.isEmpty()) {
            throw new BidNotFoundExceptions("Bid car not found with ID: " + bidCarId);
        }
        List<PlacedBid> topThreeBids = placedBidRepo.findTop3ByBidCarIdOrderByAmountDesc(bidCarId);
        return topThreeBids.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PlacedBid convertToEntity(PlacedBidDTO placedBidDTO){
        PlacedBid toEntity = modelMapper.map(placedBidDTO, PlacedBid.class);
        return toEntity;
    }

    public PlacedBidDTO convertToDto(PlacedBid placedBid){
        PlacedBidDTO toDto = modelMapper.map(placedBid, PlacedBidDTO.class);
        return toDto;
    }


}