package com.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.entity.Trainer;
import com.formation.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/trainers")
@Tag(name = "Trainer Management", description = "APIs for managing trainers")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
    RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TrainerController {
    @Autowired
    private TrainerService trainerService;

    @Operation(summary = "Create a new trainer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trainer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation failed"),
        @ApiResponse(responseCode = "409", description = "Trainer with same email already exists")
    })
    @PostMapping
    public ResponseEntity<Trainer> createTrainer(
            @Parameter(description = "Trainer to create", required = true) 
            @Valid @RequestBody(required = true) Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException("Request body cannot be null");
        }
        return new ResponseEntity<>(trainerService.save(trainer), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a trainer by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainer found"),
        @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(
            @Parameter(description = "ID of the trainer") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        return ResponseEntity.ok(trainerService.findById(id));
    }

    @Operation(summary = "Get all trainers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No trainers found")
    })
    @GetMapping
    public ResponseEntity<Page<Trainer>> getAllTrainers(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName", direction = Sort.Direction.ASC) 
            Pageable pageable) {
        Page<Trainer> trainers = trainerService.findAll(pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a trainer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Trainer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(
            @Parameter(description = "ID of the trainer to update") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id,
            @Parameter(description = "Updated trainer details") 
            @Valid @RequestBody(required = true) Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException("Request body cannot be null");
        }
        trainer.setId(id);
        return ResponseEntity.ok(trainerService.update(trainer));
    }

    @Operation(summary = "Delete a trainer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Trainer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Trainer not found"),
        @ApiResponse(responseCode = "400", description = "Trainer cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(
            @Parameter(description = "ID of the trainer to delete") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        trainerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search trainers by keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No matching trainers found")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<Trainer>> searchTrainers(
            @Parameter(description = "Search keyword") 
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") String keyword,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.search(keyword, pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get trainers by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No trainers found with this email")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Page<Trainer>> getTrainersByEmail(
            @Parameter(description = "Trainer's email") 
            @PathVariable @Email(message = "Invalid email format") String email,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.findByEmail(email, pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get trainers by specialty")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No trainers found with this specialty")
    })
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<Page<Trainer>> getTrainersBySpecialty(
            @Parameter(description = "Trainer's specialty") 
            @PathVariable @NotBlank(message = "Specialty cannot be empty") String specialty,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.findBySpecialty(specialty, pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get trainers by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No trainers found with these names")
    })
    @GetMapping("/name")
    public ResponseEntity<Page<Trainer>> getTrainersByName(
            @Parameter(description = "Trainer's last name") 
            @RequestParam @NotBlank(message = "Last name cannot be empty") String lastName,
            @Parameter(description = "Trainer's first name") 
            @RequestParam @NotBlank(message = "First name cannot be empty") String firstName,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.findByLastNameAndFirstName(lastName, firstName, pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get trainers by classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No trainers found in this classroom")
    })
    @GetMapping("/classroom/{classRoomId}")
    public ResponseEntity<Page<Trainer>> getTrainersByClassRoom(
            @Parameter(description = "Classroom ID") 
            @PathVariable @Min(value = 1, message = "Classroom ID must be positive") Long classRoomId,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.findByClassRoomId(classRoomId, pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get available trainers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No available trainers found")
    })
    @GetMapping("/available")
    public ResponseEntity<Page<Trainer>> getAvailableTrainers(
            @Parameter(description = "Maximum number of courses") 
            @RequestParam @Min(value = 0, message = "Max courses cannot be negative") int maxCourses,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.findAvailableTrainers(maxCourses, pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get trainers without assigned courses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No available trainers found")
    })
    @GetMapping("/without-courses")
    public ResponseEntity<Page<Trainer>> getTrainersWithoutCourses(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Trainer> trainers = trainerService.findTrainersWithoutCourses(pageable);
        return trainers.hasContent() 
            ? ResponseEntity.ok(trainers)
            : ResponseEntity.noContent().build();
    }
}