package com.spring.jwt.Bidding.Interface;

import com.spring.jwt.Bidding.DTO.SalesPersonDto;
import com.spring.jwt.dto.InspectorProfileDto;
import org.springframework.data.domain.Page;

public interface SalesPersonService {

    public SalesPersonDto getProfileByUserId(Integer userId);

    public Page<SalesPersonDto> getAllProfiles(Integer pageNo, Integer pageSize);

    public String updateProfile(SalesPersonDto salesPersonDto, Integer salesPersonId);

    public String deleteProfile(Integer salePersonId);

}