package com.example.wastemanagement.controller;

import com.example.wastemanagement.dto.WasteCollectionDTO;
import com.example.wastemanagement.entity.CollectionStatus;
import com.example.wastemanagement.entity.WasteType;
import com.example.wastemanagement.service.WasteCollectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class WasteCollectionController {

    @Autowired
    private WasteCollectionService collectionService;

    @GetMapping
    public ResponseEntity<List<WasteCollectionDTO>> getAllCollections(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) CollectionStatus status,
            @RequestParam(required = false) WasteType wasteType,
            @RequestParam(required = false) Long citizenId) {

        if (area != null && !area.isBlank()) {
            return ResponseEntity.ok(collectionService.getCollectionsByArea(area));
        }
        if (status != null) {
            return ResponseEntity.ok(collectionService.getCollectionsByStatus(status));
        }
        if (wasteType != null) {
            return ResponseEntity.ok(collectionService.getCollectionsByWasteType(wasteType));
        }
        if (citizenId != null) {
            return ResponseEntity.ok(collectionService.getCollectionsByCitizen(citizenId));
        }
        return ResponseEntity.ok(collectionService.getAllCollections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteCollectionDTO> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getCollectionById(id));
    }

    @PostMapping
    public ResponseEntity<WasteCollectionDTO> createCollection(@Valid @RequestBody WasteCollectionDTO dto) {
        return new ResponseEntity<>(collectionService.createCollection(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WasteCollectionDTO> updateCollection(@PathVariable Long id,
                                                                 @Valid @RequestBody WasteCollectionDTO dto) {
        return ResponseEntity.ok(collectionService.updateCollection(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}
