package com.spring.jwt.Bidding.Service;

import com.spring.jwt.Bidding.DTO.SalesPersonDto;
import com.spring.jwt.Bidding.Interface.SalesPersonService;
import com.spring.jwt.Bidding.Repository.SalesPersonRepository;
import com.spring.jwt.dto.InspectorProfileDto;
import com.spring.jwt.entity.InspectorProfile;
import com.spring.jwt.entity.SalesPerson;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesPersonServiceImpl implements SalesPersonService {

    private final ModelMapper modelMapper;

    private final SalesPersonRepository salesPersonRepository;

    private final UserRepository userRepository;


    @Override
    public SalesPersonDto getProfileByUserId(Integer userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundExceptions("User not found"));

        SalesPerson salesPerson = user.getSalesPerson();
        if (salesPerson == null) {
            throw new RuntimeException( "Sales Person not found for user " + userId);
        }


        return convertToDto(salesPerson);
    }

    @Override
    public Page<SalesPersonDto> getAllProfiles(Integer pageNo, Integer pageSize) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<SalesPerson> allProfiles = salesPersonRepository.findAll(pageable);

            int totalPages = (int) Math.ceil((double) allProfiles.getTotalElements() / pageSize);

            if (pageNo >= totalPages) {
                throw new PageNotFoundException("Page number " + pageNo + " exceeds available pages");
            }

            if (!allProfiles.hasContent()) {
                throw new RuntimeException("No data found for page: " + pageNo);
            }
            List<SalesPersonDto> all = allProfiles.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(all, pageable, allProfiles.getTotalElements());
        }

    @Override
    public String updateProfile(SalesPersonDto salesPersonDto, Integer salesPersonId) {
        SalesPerson profile = salesPersonRepository.findById(salesPersonId).orElseThrow(() -> new UserNotFoundExceptions("Profile not found", HttpStatus.NOT_FOUND));

        if (salesPersonDto.getAddress() != null && !salesPersonDto.getAddress().isEmpty()) {
            profile.setAddress(salesPersonDto.getAddress());
        }
        if (salesPersonDto.getCity() != null && !salesPersonDto.getCity().isEmpty()) {
            profile.setCity(salesPersonDto.getCity());
        }
        if (salesPersonDto.getFirstName() != null &&!salesPersonDto.getFirstName().isEmpty()) {
            profile.setFirstName(salesPersonDto.getFirstName());
        }
        if (salesPersonDto.getLastName() != null && !salesPersonDto.getLastName().isEmpty()) {
            profile.setLastName(salesPersonDto.getLastName());
        }
        if (salesPersonDto.getArea() != null && !salesPersonDto.getArea().isEmpty()) {
            profile.setArea(salesPersonDto.getArea());
        }
        salesPersonRepository.save(profile);

        User user = profile.getUser();
        if (user != null) {
            if (salesPersonDto.getMobileNo() != null && !salesPersonDto.getMobileNo().isEmpty()) {
                user.setMobileNo(salesPersonDto.getMobileNo());
            }
            if (salesPersonDto.getEmail() != null && !salesPersonDto.getEmail().isEmpty()) {
                user.setEmail(salesPersonDto.getEmail());
            }
            userRepository.save(user);
        }

        return "Profile fields updated successfully";
    }

    @Override
    public String deleteProfile(Integer inspectorProfileId) {
        SalesPerson profiles = salesPersonRepository.findById(inspectorProfileId).orElseThrow(() -> new UserNotFoundExceptions("Profile Not Found"));
        salesPersonRepository.deleteById(inspectorProfileId);
        User user = profiles.getUser();
        user.getRoles().clear();
        userRepository.deleteById(user.getId());
        return "Profile deleted successfully";
    }


    private SalesPersonDto convertToDto(SalesPerson salesPerson) {
        SalesPersonDto dto = new SalesPersonDto();
        dto.setSalesPersonId(salesPerson.getSalesPersonId());
        dto.setAddress(salesPerson.getAddress());
        dto.setCity(salesPerson.getCity());
        dto.setFirstName(salesPerson.getFirstName());
        dto.setLastName(salesPerson.getLastName());
        dto.setArea(salesPerson.getArea());
        dto.setDocumentId(salesPerson.getDocumentId());
        dto.setJoiningdate(salesPerson.getJoiningdate());
        dto.setStatus(salesPerson.getStatus());

        User user = salesPerson.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setMobileNo(user.getMobileNo());

        }
        return dto;
    }
}
