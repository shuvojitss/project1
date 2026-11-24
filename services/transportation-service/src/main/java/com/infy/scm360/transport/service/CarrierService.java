package com.infy.scm360.transport.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.transport.domain.Carrier;
import com.infy.scm360.transport.dto.CarrierDto;
import com.infy.scm360.transport.repository.CarrierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarrierService {

    private final CarrierRepository carrierRepository;

    public CarrierService(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    public CarrierDto createCarrier(CarrierDto dto) {
        Carrier carrier = new Carrier();
        carrier.setCarrierCode(dto.carrierCode());
        carrier.setName(dto.name());
        carrier.setCarrierType(dto.carrierType());
        carrier.setContactEmail(dto.contactEmail());
        carrier.setContactPhone(dto.contactPhone());
        if (dto.serviceLevel() != null) carrier.setServiceLevel(dto.serviceLevel());
        carrier.setActive(dto.active());
        if (dto.rating() != null) carrier.setRating(dto.rating());

        Carrier saved = carrierRepository.save(carrier);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public CarrierDto getCarrier(String id) {
        return carrierRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<CarrierDto> listCarriers(Pageable pageable) {
        return carrierRepository.findByActiveTrue(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CarrierDto> listAllActiveCarriers() {
        return carrierRepository.findByActiveTrue().stream().map(this::toDto).toList();
    }

    public CarrierDto toDto(Carrier carrier) {
        return new CarrierDto(
                carrier.getId(),
                carrier.getCarrierCode(),
                carrier.getName(),
                carrier.getCarrierType(),
                carrier.getContactEmail(),
                carrier.getContactPhone(),
                carrier.getServiceLevel(),
                carrier.isActive(),
                carrier.getRating()
        );
    }
}
