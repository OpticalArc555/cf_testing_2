package com.spring.jwt.Interfaces;

import com.spring.jwt.dto.InspectorProfileDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InspectorProfileService {
    public String updateProfile(InspectorProfileDto inspectorProfileDto, Integer InspectorProfileId);

    public InspectorProfileDto getProfileById(Integer inspectorProfileId);

    public String deleteProfile(Integer inspectorProfileId);

    public InspectorProfileDto getProfileByUserId(Integer userId);

    public Page<InspectorProfileDto> getAllProfiles(Integer pageNo, Integer pageSize);

}


