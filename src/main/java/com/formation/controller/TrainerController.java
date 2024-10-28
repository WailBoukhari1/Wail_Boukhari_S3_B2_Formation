package com.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.entity.Trainer;
import com.formation.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/trainers")
@Tag(name = "Trainer Management", description = "APIs for managing trainers")
public class TrainerController {
    @Autowired
    private TrainerService trainerService;
    @Operation(summary = "Create a new trainer")
    @ApiResponse(responseCode = "201", description = "Trainer created successfully")
    @PostMapping
    public ResponseEntity<Trainer> createTrainer(@Valid @RequestBody Trainer trainer)
{
        return new ResponseEntity<>(trainerService.save(trainer), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Trainer>> getAllTrainers(Pageable pageable) {
        return ResponseEntity.ok(trainerService.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(
            @PathVariable Long id, 
            @Valid @RequestBody Trainer trainer) {
        trainer.setId(id);
        return ResponseEntity.ok(trainerService.update(trainer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Trainer>> searchTrainers(
            @RequestParam String keyword, 
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.search(keyword, pageable));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Page<Trainer>> getTrainersByEmail(
            @PathVariable String email,
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.findByEmail(email, pageable));
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<Page<Trainer>> getTrainersBySpecialty(
            @PathVariable String specialty,
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.findBySpecialty(specialty, pageable));
    }

    @GetMapping("/name")
    public ResponseEntity<Page<Trainer>> getTrainersByName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.findByLastNameAndFirstName(lastName, firstName, pageable));
    }

    @GetMapping("/classroom/{classRoomId}")
    public ResponseEntity<Page<Trainer>> getTrainersByClassRoom(
            @PathVariable Long classRoomId, 
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.findByClassRoomId(classRoomId, pageable));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<Trainer>> getAvailableTrainers(
            @RequestParam int maxCourses, 
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.findAvailableTrainers(maxCourses, pageable));
    }

    @GetMapping("/without-courses")
    public ResponseEntity<Page<Trainer>> getTrainersWithoutCourses(
            Pageable pageable) {
        return ResponseEntity.ok(trainerService.findTrainersWithoutCourses(pageable));
    }
}
