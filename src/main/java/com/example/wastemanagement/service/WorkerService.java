package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.WorkerDTO;
import com.example.wastemanagement.entity.Worker;
import com.example.wastemanagement.entity.WorkerStatus;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    public List<WorkerDTO> getAllWorkers() {
        return workerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public WorkerDTO getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));
        return toDTO(worker);
    }

    public WorkerDTO createWorker(WorkerDTO dto) {
        Worker worker = new Worker();
        worker.setName(dto.getName());
        worker.setPhone(dto.getPhone());
        worker.setAssignedArea(dto.getAssignedArea());
        worker.setStatus(dto.getStatus() != null ? dto.getStatus() : WorkerStatus.ACTIVE);
        return toDTO(workerRepository.save(worker));
    }

    public WorkerDTO updateWorker(Long id, WorkerDTO dto) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));
        worker.setName(dto.getName());
        worker.setPhone(dto.getPhone());
        worker.setAssignedArea(dto.getAssignedArea());
        if (dto.getStatus() != null) {
            worker.setStatus(dto.getStatus());
        }
        return toDTO(workerRepository.save(worker));
    }

    public void deleteWorker(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));
        workerRepository.delete(worker);
    }

    private WorkerDTO toDTO(Worker worker) {
        WorkerDTO dto = new WorkerDTO();
        dto.setWorkerId(worker.getWorkerId());
        dto.setName(worker.getName());
        dto.setPhone(worker.getPhone());
        dto.setAssignedArea(worker.getAssignedArea());
        dto.setStatus(worker.getStatus());
        return dto;
    }
}
