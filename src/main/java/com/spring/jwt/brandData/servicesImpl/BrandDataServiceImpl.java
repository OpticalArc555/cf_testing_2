package com.spring.jwt.brandData.servicesImpl;

import com.spring.jwt.brandData.Interface.BrandDataService;
import com.spring.jwt.brandData.Dto.BrandDataDto;
import com.spring.jwt.brandData.Dto.onlyBrandDto;
import com.spring.jwt.brandData.Entity.BrandData;
import com.spring.jwt.brandData.exception.BrandNotFoundException;
import com.spring.jwt.brandData.Reposiory.BrandDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandDataServiceImpl implements BrandDataService {

    @Autowired
    private BrandDataRepository brandDataRepository;

    @Override
    public BrandDataDto addBrand(BrandDataDto brandDataDto) {
        Optional<BrandData> existingBrand = brandDataRepository.findById(brandDataDto.getBrandDataId());
        if (existingBrand.isPresent()) {
            throw new BrandNotFoundException("Brand with ID " + brandDataDto.getBrandDataId() + " already exists");
        }
        BrandData brandData = new BrandData();
        brandData.setBrandDataId(brandDataDto.getBrandDataId());
        brandData.setBrand(brandDataDto.getBrand());
        brandData.setVariant(brandDataDto.getVariant());
        brandData.setSubVariant(brandDataDto.getSubVariant());
        brandDataRepository.save(brandData);
        return brandDataDto;
    }

    @Override
    public List<BrandDataDto> GetAllBrands() {
        List<BrandData> brands = brandDataRepository.findAll();
        return brands.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public String editBrand(Integer brandDataId, BrandDataDto brandDataDto) {
        Optional<BrandData> brandDataOptional = brandDataRepository.findById(brandDataId);
        if (!brandDataOptional.isPresent()) {
            throw new BrandNotFoundException("Brand with ID " + brandDataId + " not found");
        }
        BrandData brandData = brandDataOptional.get();
        if (brandDataDto.getBrand() != null) {
            brandData.setBrand(brandDataDto.getBrand());
        }
        if (brandDataDto.getVariant() != null) {
            brandData.setVariant(brandDataDto.getVariant());
        }
        if (brandDataDto.getSubVariant() != null) {
            brandData.setSubVariant(brandDataDto.getSubVariant());
        }

        brandDataRepository.save(brandData);
        return "Brand edited successfully";
    }

    @Override
    public String deleteBrand(Integer brandDataId) {
        Optional<BrandData> brandDataOptional = brandDataRepository.findById(brandDataId);
        if (!brandDataOptional.isPresent()) {
            throw new BrandNotFoundException("Brand with ID " + brandDataId + " not found");
        }
        brandDataRepository.deleteById(brandDataId);
        return "Brand deleted successfully";
    }


    @Override
    public List<onlyBrandDto> onlyBrands() {
        List<String> brands = brandDataRepository.findDistinctBrands();
        return brands.stream()
                .map(onlyBrandDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BrandDataDto> variants(String brand) {
        List<BrandData> variants = brandDataRepository.findByBrand(brand);
        if (variants.isEmpty()) {
            throw new BrandNotFoundException("No variants found for brand " + brand);
        }
        return variants.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<BrandDataDto> subVariant(String brand, String variant) {
        List<BrandData> subVariants = brandDataRepository.findByBrandAndVariant(brand, variant);
        if (subVariants.isEmpty()) {
            throw new BrandNotFoundException("No sub-variants found for brand " + brand + " and variant " + variant);
        }
        return subVariants.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private BrandDataDto convertToDto(BrandData brandData) {
        BrandDataDto dto = new BrandDataDto();
        dto.setBrandDataId(brandData.getBrandDataId());
        dto.setBrand(brandData.getBrand());
        dto.setVariant(brandData.getVariant());
        dto.setSubVariant(brandData.getSubVariant());
        return dto;
    }
}
