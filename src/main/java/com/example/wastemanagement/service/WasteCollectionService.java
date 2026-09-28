package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.WasteCollectionDTO;
import com.example.wastemanagement.entity.*;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.CitizenRepository;
import com.example.wastemanagement.repository.VehicleRepository;
import com.example.wastemanagement.repository.WasteCollectionRepository;
import com.example.wastemanagement.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WasteCollectionService {

    @Autowired
    private WasteCollectionRepository collectionRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<WasteCollectionDTO> getAllCollections() {
        return collectionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public WasteCollectionDTO getCollectionById(Long id) {
        WasteCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste collection", id));
        return toDTO(collection);
    }

    public List<WasteCollectionDTO> getCollectionsByCitizen(Long citizenId) {
        return collectionRepository.findByCitizen_CitizenId(citizenId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<WasteCollectionDTO> getCollectionsByArea(String area) {
        return collectionRepository.findByAreaIgnoreCase(area)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<WasteCollectionDTO> getCollectionsByStatus(CollectionStatus status) {
        return collectionRepository.findByStatus(status)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<WasteCollectionDTO> getCollectionsByWasteType(WasteType type) {
        return collectionRepository.findByWasteType(type)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public WasteCollectionDTO createCollection(WasteCollectionDTO dto) {
        WasteCollection collection = new WasteCollection();
        applyDtoToEntity(dto, collection);
        return toDTO(collectionRepository.save(collection));
    }

    public WasteCollectionDTO updateCollection(Long id, WasteCollectionDTO dto) {
        WasteCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste collection", id));
        applyDtoToEntity(dto, collection);
        return toDTO(collectionRepository.save(collection));
    }

    public void deleteCollection(Long id) {
        WasteCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste collection", id));
        collectionRepository.delete(collection);
    }

    private void applyDtoToEntity(WasteCollectionDTO dto, WasteCollection collection) {
        Citizen citizen = citizenRepository.findById(dto.getCitizenId())
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", dto.getCitizenId()));
        collection.setCitizen(citizen);

        if (dto.getWorkerId() != null) {
            Worker worker = workerRepository.findById(dto.getWorkerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Worker", dto.getWorkerId()));
            collection.setWorker(worker);
        } else {
            collection.setWorker(null);
        }

        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", dto.getVehicleId()));
            collection.setVehicle(vehicle);
        } else {
            collection.setVehicle(null);
        }

        collection.setCollectionDate(dto.getCollectionDate());
        collection.setArea(dto.getArea());
        collection.setWasteType(dto.getWasteType());
        collection.setQuantity(dto.getQuantity());
        collection.setStatus(dto.getStatus() != null ? dto.getStatus() : CollectionStatus.PENDING);
    }

    private WasteCollectionDTO toDTO(WasteCollection collection) {
        WasteCollectionDTO dto = new WasteCollectionDTO();
        dto.setCollectionId(collection.getCollectionId());
        dto.setCitizenId(collection.getCitizen().getCitizenId());
        dto.setCitizenName(collection.getCitizen().getName());
        if (collection.getWorker() != null) {
            dto.setWorkerId(collection.getWorker().getWorkerId());
            dto.setWorkerName(collection.getWorker().getName());
        }
        if (collection.getVehicle() != null) {
            dto.setVehicleId(collection.getVehicle().getVehicleId());
            dto.setVehicleNumber(collection.getVehicle().getVehicleNumber());
        }
        dto.setCollectionDate(collection.getCollectionDate());
        dto.setArea(collection.getArea());
        dto.setWasteType(collection.getWasteType());
        dto.setQuantity(collection.getQuantity());
        dto.setStatus(collection.getStatus());
        dto.setSegregated(collection.getSegregation() != null);
        return dto;
    }
}
