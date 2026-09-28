package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.SegregationDTO;
import com.example.wastemanagement.entity.Segregation;
import com.example.wastemanagement.entity.SegregationStatus;
import com.example.wastemanagement.entity.WasteCollection;
import com.example.wastemanagement.exception.BadRequestException;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.SegregationRepository;
import com.example.wastemanagement.repository.WasteCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SegregationService {

    @Autowired
    private SegregationRepository segregationRepository;

    @Autowired
    private WasteCollectionRepository wasteCollectionRepository;

    public List<SegregationDTO> getAllSegregations() {
        return segregationRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SegregationDTO getSegregationById(Long id) {
        Segregation segregation = segregationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segregation record", id));
        return toDTO(segregation);
    }

    public SegregationDTO createSegregation(SegregationDTO dto) {
        WasteCollection collection = wasteCollectionRepository.findById(dto.getCollectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste collection", dto.getCollectionId()));

        if (segregationRepository.existsByWasteCollection_CollectionId(dto.getCollectionId())) {
            throw new BadRequestException(
                    "A segregation record already exists for collection ID " + dto.getCollectionId());
        }

        Segregation segregation = new Segregation();
        segregation.setWasteCollection(collection);
        applyQuantities(dto, segregation);
        Segregation saved = segregationRepository.save(segregation);
        return toDTO(saved);
    }

    public SegregationDTO updateSegregation(Long id, SegregationDTO dto) {
        Segregation segregation = segregationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segregation record", id));
        applyQuantities(dto, segregation);
        return toDTO(segregationRepository.save(segregation));
    }

    public void deleteSegregation(Long id) {
        Segregation segregation = segregationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segregation record", id));
        segregationRepository.delete(segregation);
    }

    /**
     * Applies the submitted quantities and automatically derives the
     * segregation status when the caller did not explicitly provide one:
     * - PROPER   : waste was split across more than one category (properly separated)
     * - PARTIAL  : mostly one category but a little of another is mixed in
     * - IMPROPER : effectively dumped as a single undifferentiated category
     */
    private void applyQuantities(SegregationDTO dto, Segregation segregation) {
        double wet = nullToZero(dto.getWetQuantity());
        double dry = nullToZero(dto.getDryQuantity());
        double plastic = nullToZero(dto.getPlasticQuantity());
        double paper = nullToZero(dto.getPaperQuantity());
        double other = nullToZero(dto.getOtherQuantity());

        segregation.setWetQuantity(wet);
        segregation.setDryQuantity(dry);
        segregation.setPlasticQuantity(plastic);
        segregation.setPaperQuantity(paper);
        segregation.setOtherQuantity(other);

        if (dto.getSegregationStatus() != null) {
            segregation.setSegregationStatus(dto.getSegregationStatus());
        } else {
            segregation.setSegregationStatus(deriveStatus(wet, dry, plastic, paper, other));
        }
    }

    private SegregationStatus deriveStatus(double wet, double dry, double plastic, double paper, double other) {
        double total = wet + dry + plastic + paper + other;
        if (total <= 0) {
            return SegregationStatus.IMPROPER;
        }
        long categoriesUsed = 0;
        for (double v : new double[]{wet, dry, plastic, paper, other}) {
            if (v > 0) {
                categoriesUsed++;
            }
        }
        if (categoriesUsed >= 3) {
            return SegregationStatus.PROPER;
        } else if (categoriesUsed == 2) {
            return SegregationStatus.PARTIAL;
        }
        return SegregationStatus.IMPROPER;
    }

    private double nullToZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private SegregationDTO toDTO(Segregation segregation) {
        SegregationDTO dto = new SegregationDTO();
        dto.setSegregationId(segregation.getSegregationId());
        dto.setCollectionId(segregation.getWasteCollection().getCollectionId());
        dto.setWetQuantity(segregation.getWetQuantity());
        dto.setDryQuantity(segregation.getDryQuantity());
        dto.setPlasticQuantity(segregation.getPlasticQuantity());
        dto.setPaperQuantity(segregation.getPaperQuantity());
        dto.setOtherQuantity(segregation.getOtherQuantity());
        dto.setSegregationStatus(segregation.getSegregationStatus());
        dto.setTotalQuantity(segregation.getTotalQuantity());
        return dto;
    }
}
