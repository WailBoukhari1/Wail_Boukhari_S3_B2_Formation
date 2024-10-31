package com.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.formation.entity.ClassRoom;
import com.formation.service.ClassRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/classrooms")
@Tag(name = "Classroom Management", description = "APIs for managing classrooms")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
    RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ClassRoomController {

    @Autowired
    private ClassRoomService classRoomService;

    @Operation(summary = "Create a new classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Classroom created successfully",
            content = @Content(schema = @Schema(implementation = ClassRoom.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation failed"),
        @ApiResponse(responseCode = "409", description = "Classroom with same name/number already exists")
    })
    @PostMapping
    public ResponseEntity<ClassRoom> createClassRoom(
            @Parameter(description = "Classroom to create", required = true) 
            @Valid @RequestBody(required = true) ClassRoom classRoom) {
        if (classRoom == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        try {
            return new ResponseEntity<>(classRoomService.save(classRoom), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Classroom with number " + classRoom.getRoomNumber() + " already exists");
        }
    }

    @Operation(summary = "Get a classroom by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classroom found"),
        @ApiResponse(responseCode = "404", description = "Classroom not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassRoom> getClassRoomById(
            @Parameter(description = "ID of the classroom") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(classRoomService.findById(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Classroom not found with id: " + id);
        }
    }

    @Operation(summary = "Get all classrooms with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classrooms retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No classrooms found"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    public ResponseEntity<Page<ClassRoom>> getAllClassRooms(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ClassRoom> classrooms = classRoomService.findAll(pageable);
        return classrooms.hasContent() 
            ? ResponseEntity.ok(classrooms)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classroom updated successfully"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClassRoom> updateClassRoom(
            @Parameter(description = "ID of the classroom to update") @PathVariable Long id,
            @Parameter(description = "Updated classroom details") @Valid @RequestBody ClassRoom classRoom) {
        if (classRoom == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        try {
            classRoom.setId(id);
            return ResponseEntity.ok(classRoomService.update(classRoom));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Classroom not found with id: " + id);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Classroom with number " + classRoom.getRoomNumber() + " already exists");
        }
    }

    @Operation(summary = "Delete a classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Classroom deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "409", description = "Classroom cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassRoom(
            @Parameter(description = "ID of the classroom to delete") @PathVariable Long id) {
        try {
            classRoomService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("enrolled students")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Cannot delete classroom with enrolled students");
            } else if (e.getMessage().contains("assigned trainers")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Cannot delete classroom with assigned trainers");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Classroom not found with id: " + id);
        }
    }

    @Operation(summary = "Search classrooms by keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ClassRoom>> searchClassRooms(
            @Parameter(description = "Search keyword") 
            @RequestParam(required = true) @NotBlank String keyword,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        if (keyword.trim().length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        Page<ClassRoom> results = classRoomService.search(keyword, pageable);
        return results.hasContent() ? ResponseEntity.ok(results) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get available rooms by capacity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available rooms retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid capacity parameter")
    })
    @GetMapping("/available")
    public ResponseEntity<Page<ClassRoom>> getAvailableRooms(
            @Parameter(description = "Required capacity") 
            @RequestParam @Min(value = 1, message = "Capacity must be at least 1") int capacity,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "currentCapacity") Pageable pageable) {
        Page<ClassRoom> rooms = classRoomService.findAvailableRooms(capacity, pageable);
        return rooms.hasContent() ? ResponseEntity.ok(rooms) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get empty classrooms")
    @ApiResponse(responseCode = "200", description = "Empty rooms retrieved successfully")
    @GetMapping("/empty")
    public ResponseEntity<Page<ClassRoom>> getEmptyRooms(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<ClassRoom> rooms = classRoomService.findEmptyRooms(pageable);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Get classrooms without trainers")
    @ApiResponse(responseCode = "200", description = "Classrooms without trainers retrieved successfully")
    @GetMapping("/without-trainers")
    public ResponseEntity<Page<ClassRoom>> getRoomsWithoutTrainers(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<ClassRoom> rooms = classRoomService.findRoomsWithoutTrainers(pageable);
        return ResponseEntity.ok(rooms);
    }
}
