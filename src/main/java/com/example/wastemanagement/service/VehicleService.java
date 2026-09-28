package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.VehicleDTO;
import com.example.wastemanagement.entity.Vehicle;
import com.example.wastemanagement.entity.VehicleStatus;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        return toDTO(vehicle);
    }

    public VehicleDTO createVehicle(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(dto.getVehicleNumber());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setDriverName(dto.getDriverName());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setStatus(dto.getStatus() != null ? dto.getStatus() : VehicleStatus.AVAILABLE);
        return toDTO(vehicleRepository.save(vehicle));
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        vehicle.setVehicleNumber(dto.getVehicleNumber());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setDriverName(dto.getDriverName());
        vehicle.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) {
            vehicle.setStatus(dto.getStatus());
        }
        return toDTO(vehicleRepository.save(vehicle));
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        vehicleRepository.delete(vehicle);
    }

    private VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setVehicleNumber(vehicle.getVehicleNumber());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setDriverName(vehicle.getDriverName());
        dto.setCapacity(vehicle.getCapacity());
        dto.setStatus(vehicle.getStatus());
        return dto;
    }
}
