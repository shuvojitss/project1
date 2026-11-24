package com.infy.scm360.transport.service;

import com.infy.scm360.transport.domain.Carrier;
import com.infy.scm360.transport.domain.RateCardEntry;
import com.infy.scm360.transport.dto.RateCalculationRequest;
import com.infy.scm360.transport.dto.RateQuoteDto;
import com.infy.scm360.transport.repository.CarrierRepository;
import com.infy.scm360.transport.repository.RateCardEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RateService {

    private final RateCardEntryRepository rateCardRepository;
    private final CarrierRepository carrierRepository;

    public RateService(RateCardEntryRepository rateCardRepository, CarrierRepository carrierRepository) {
        this.rateCardRepository = rateCardRepository;
        this.carrierRepository = carrierRepository;
    }

    public List<RateQuoteDto> calculateBestRates(RateCalculationRequest request) {
        List<RateCardEntry> entries = rateCardRepository.findByOriginRegionAndDestinationRegion(
                request.originRegion(), request.destinationRegion()
        );

        Map<String, Carrier> carriers = carrierRepository.findByActiveTrue().stream()
                .collect(Collectors.toMap(Carrier::getId, c -> c));

        List<RateQuoteDto> quotes = new ArrayList<>();
        BigDecimal weight = request.weightKg();

        for (RateCardEntry entry : entries) {
            if (weight.compareTo(entry.getMaxWeightKg()) <= 0) {
                Carrier carrier = carriers.get(entry.getCarrierId());
                if (carrier != null && (request.serviceLevel() == null || request.serviceLevel().isBlank() || carrier.getServiceLevel().equalsIgnoreCase(request.serviceLevel()))) {
                    BigDecimal cost = entry.calculateCost(weight);
                    quotes.add(new RateQuoteDto(
                            carrier.getId(),
                            carrier.getCarrierCode(),
                            carrier.getName(),
                            carrier.getServiceLevel(),
                            cost,
                            entry.getCurrency(),
                            entry.getTransitDays()
                    ));
                }
            }
        }

        // Sort by estimated cost ascending
        quotes.sort(Comparator.comparing(RateQuoteDto::estimatedCost));
        return quotes;
    }
}
