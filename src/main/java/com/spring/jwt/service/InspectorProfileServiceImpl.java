package com.spring.jwt.service;

import com.spring.jwt.Interfaces.InspectorProfileService;
import com.spring.jwt.dto.InspectorProfileDto;
import com.spring.jwt.entity.InspectorProfile;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.PageNotFoundException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.InspectorProfileRepo;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectorProfileServiceImpl implements InspectorProfileService {

    private final InspectorProfileRepo inspectorProfileRepo;

    private final UserRepository userRepository;

    @Override
    public String updateProfile(InspectorProfileDto inspectorProfileDto, Integer InspectorProfileId) {
        InspectorProfile profile = inspectorProfileRepo.findById(InspectorProfileId).orElseThrow(() -> new UserNotFoundExceptions("Profile not found", HttpStatus.NOT_FOUND));


        if (inspectorProfileDto.getAddress() != null) {
            profile.setAddress(inspectorProfileDto.getAddress());
        }
        if (inspectorProfileDto.getCity() != null) {
            profile.setCity(inspectorProfileDto.getCity());
        }
        if (inspectorProfileDto.getFirstName() != null) {
            profile.setFirstName(inspectorProfileDto.getFirstName());
        }
        if (inspectorProfileDto.getLastName() != null) {
            profile.setLastName(inspectorProfileDto.getLastName());
        }

        inspectorProfileRepo.save(profile);

        User user = profile.getUser();

        if (user != null) {
            if (inspectorProfileDto.getMobileNo() != null) {
                user.setMobileNo(inspectorProfileDto.getMobileNo());
            }
            if (inspectorProfileDto.getEmail() != null) {
                user.setEmail(inspectorProfileDto.getEmail());
            }
            userRepository.save(user);
        }

        return "Profile fields updated successfully";
    }

    @Override
    public InspectorProfileDto getProfileById(Integer inspectorProfileId) {
        Optional<InspectorProfile> profileOptional = inspectorProfileRepo.findById(inspectorProfileId);
        if (profileOptional.isPresent()) {
            InspectorProfile profile = profileOptional.get();
            User user = profile.getUser();

            InspectorProfileDto profileDto = new InspectorProfileDto();
            profileDto.setAddress(profile.getAddress());
            profileDto.setCity(profile.getCity());
            profileDto.setFirstName(profile.getFirstName());
            profileDto.setLastName(profile.getLastName());

            if (user != null) {
                profileDto.setEmail(user.getEmail());
                profileDto.setMobileNo(user.getMobileNo());
            }

            return profileDto;
        } else {
            throw new UserNotFoundExceptions("Profile Not Found");
        }
    }

    @Override
    public String deleteProfile(Integer inspectorProfileId) {
        InspectorProfile profiles = inspectorProfileRepo.findById(inspectorProfileId).orElseThrow(() -> new UserNotFoundExceptions("Profile Not Found"));
        inspectorProfileRepo.deleteById(inspectorProfileId);
        User user = profiles.getUser();
        user.getRoles().clear();
        userRepository.deleteById(user.getId());


        return "Profile deleted successfully";
    }

    @Override
    public InspectorProfileDto getProfileByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found"));

        InspectorProfile inspectorProfile = user.getInspectorProfile();
        if (inspectorProfile == null) {
            throw new RuntimeException("Inspector profile not found for user with id: " + userId);
        }
        InspectorProfileDto inspectorProfileDto = new InspectorProfileDto();
        inspectorProfileDto.setAddress(inspectorProfile.getAddress());
        inspectorProfileDto.setCity(inspectorProfile.getCity());
        inspectorProfileDto.setFirstName(inspectorProfile.getFirstName());
        inspectorProfileDto.setLastName(inspectorProfile.getLastName());
        inspectorProfileDto.setEmail(user.getEmail());
        inspectorProfileDto.setMobileNo(user.getMobileNo());

        return inspectorProfileDto;
    }

    @Override
    public Page<InspectorProfileDto> getAllProfiles(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<InspectorProfile> allProfiles = inspectorProfileRepo.findAll(pageable);

        if (allProfiles.isEmpty()) {
            throw new PageNotFoundException("No Data Found");
        }

        List<InspectorProfileDto> profileDtoList = allProfiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(profileDtoList, pageable, allProfiles.getTotalElements());
    }


    private InspectorProfileDto convertToDto(InspectorProfile inspectorProfile) {
        InspectorProfileDto dto = new InspectorProfileDto();
        dto.setInspectorProfileId(inspectorProfile.getId());
        dto.setAddress(inspectorProfile.getAddress());
        dto.setCity(inspectorProfile.getCity());
        dto.setFirstName(inspectorProfile.getFirstName());
        dto.setLastName(inspectorProfile.getLastName());
        User user = inspectorProfile.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setMobileNo(user.getMobileNo());
        }
        return dto;

    }
}