package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.CitizenDTO;
import com.example.wastemanagement.entity.Citizen;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitizenService {

    @Autowired
    private CitizenRepository citizenRepository;

    public List<CitizenDTO> getAllCitizens() {
        return citizenRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CitizenDTO getCitizenById(Long id) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", id));
        return toDTO(citizen);
    }

    public CitizenDTO createCitizen(CitizenDTO dto) {
        Citizen citizen = toEntity(dto);
        Citizen saved = citizenRepository.save(citizen);
        return toDTO(saved);
    }

    public CitizenDTO updateCitizen(Long id, CitizenDTO dto) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", id));
        citizen.setName(dto.getName());
        citizen.setPhone(dto.getPhone());
        citizen.setEmail(dto.getEmail());
        citizen.setAddress(dto.getAddress());
        citizen.setArea(dto.getArea());
        citizen.setWardNumber(dto.getWardNumber());
        return toDTO(citizenRepository.save(citizen));
    }

    public void deleteCitizen(Long id) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", id));
        citizenRepository.delete(citizen);
    }

    public List<CitizenDTO> searchByName(String name) {
        return citizenRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CitizenDTO toDTO(Citizen citizen) {
        CitizenDTO dto = new CitizenDTO();
        dto.setCitizenId(citizen.getCitizenId());
        dto.setName(citizen.getName());
        dto.setPhone(citizen.getPhone());
        dto.setEmail(citizen.getEmail());
        dto.setAddress(citizen.getAddress());
        dto.setArea(citizen.getArea());
        dto.setWardNumber(citizen.getWardNumber());
        return dto;
    }

    private Citizen toEntity(CitizenDTO dto) {
        Citizen citizen = new Citizen();
        citizen.setName(dto.getName());
        citizen.setPhone(dto.getPhone());
        citizen.setEmail(dto.getEmail());
        citizen.setAddress(dto.getAddress());
        citizen.setArea(dto.getArea());
        citizen.setWardNumber(dto.getWardNumber());
        return citizen;
    }
}
