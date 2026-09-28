package com.example.wastemanagement;

import com.example.wastemanagement.dto.CitizenDTO;
import com.example.wastemanagement.entity.Citizen;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.CitizenRepository;
import com.example.wastemanagement.service.CitizenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitizenServiceTest {

    @Mock
    private CitizenRepository citizenRepository;

    @InjectMocks
    private CitizenService citizenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Citizen sampleCitizen() {
        Citizen citizen = new Citizen();
        citizen.setCitizenId(1L);
        citizen.setName("Arun Kumar");
        citizen.setPhone("9876543210");
        citizen.setEmail("arun@example.com");
        citizen.setAddress("12 Gandhi Street");
        citizen.setArea("Ward 1");
        citizen.setWardNumber(1);
        return citizen;
    }

    @Test
    void createCitizen_savesAndReturnsDTO() {
        Citizen citizen = sampleCitizen();
        when(citizenRepository.save(any(Citizen.class))).thenReturn(citizen);

        CitizenDTO dto = new CitizenDTO();
        dto.setName("Arun Kumar");
        dto.setPhone("9876543210");
        dto.setEmail("arun@example.com");
        dto.setAddress("12 Gandhi Street");
        dto.setArea("Ward 1");
        dto.setWardNumber(1);

        CitizenDTO result = citizenService.createCitizen(dto);

        assertNotNull(result);
        assertEquals("Arun Kumar", result.getName());
        verify(citizenRepository, times(1)).save(any(Citizen.class));
    }

    @Test
    void getCitizenById_returnsCitizen_whenExists() {
        Citizen citizen = sampleCitizen();
        when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));

        CitizenDTO result = citizenService.getCitizenById(1L);

        assertEquals(1L, result.getCitizenId());
        assertEquals("Arun Kumar", result.getName());
    }

    @Test
    void getCitizenById_throwsException_whenNotFound() {
        when(citizenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> citizenService.getCitizenById(99L));
    }

    @Test
    void updateCitizen_updatesFields() {
        Citizen citizen = sampleCitizen();
        when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));
        when(citizenRepository.save(any(Citizen.class))).thenAnswer(inv -> inv.getArgument(0));

        CitizenDTO dto = new CitizenDTO();
        dto.setName("Arun Kumar Updated");
        dto.setPhone("9876543210");
        dto.setEmail("arun@example.com");
        dto.setAddress("New Address");
        dto.setArea("Ward 2");
        dto.setWardNumber(2);

        CitizenDTO result = citizenService.updateCitizen(1L, dto);

        assertEquals("Arun Kumar Updated", result.getName());
        assertEquals("Ward 2", result.getArea());
    }

    @Test
    void deleteCitizen_deletesWhenExists() {
        Citizen citizen = sampleCitizen();
        when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));

        citizenService.deleteCitizen(1L);

        verify(citizenRepository, times(1)).delete(citizen);
    }

    @Test
    void deleteCitizen_throwsException_whenNotFound() {
        when(citizenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> citizenService.deleteCitizen(99L));
    }
}
