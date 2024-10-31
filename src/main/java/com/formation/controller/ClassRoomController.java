package com.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import jakarta.validation.ValidationException;
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
            throw new ValidationException("Request body cannot be null");
        }
        return new ResponseEntity<>(classRoomService.save(classRoom), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a classroom by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classroom found"),
        @ApiResponse(responseCode = "404", description = "Classroom not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassRoom> getClassRoomById(
            @Parameter(description = "ID of the classroom") @PathVariable Long id) {
        return ResponseEntity.ok(classRoomService.findById(id));
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
        classRoom.setId(id);
        return ResponseEntity.ok(classRoomService.update(classRoom));
    }

    @Operation(summary = "Delete a classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Classroom deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Classroom not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassRoom(
            @Parameter(description = "ID of the classroom to delete") @PathVariable Long id) {
        classRoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search classrooms by keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No matching classrooms found"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ClassRoom>> searchClassRooms(
            @Parameter(description = "Search keyword") 
            @RequestParam(required = true) @NotBlank(message = "Search keyword cannot be empty") String keyword,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        if (keyword.trim().length() < 2) {
            throw new ValidationException("Search keyword must be at least 2 characters long");
        }
        Page<ClassRoom> results = classRoomService.search(keyword, pageable);
        return results.hasContent() 
            ? ResponseEntity.ok(results)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get available rooms by capacity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available rooms retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No available rooms found"),
        @ApiResponse(responseCode = "400", description = "Invalid capacity parameter")
    })
    @GetMapping("/available")
    public ResponseEntity<Page<ClassRoom>> getAvailableRooms(
            @Parameter(description = "Required capacity") 
            @RequestParam @Min(value = 1, message = "Capacity must be at least 1") int capacity,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "currentCapacity") Pageable pageable) {
        Page<ClassRoom> rooms = classRoomService.findAvailableRooms(capacity, pageable);
        return rooms.hasContent() 
            ? ResponseEntity.ok(rooms)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get empty classrooms")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empty classrooms retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No empty classrooms found")
    })
    @GetMapping("/empty")
    public ResponseEntity<Page<ClassRoom>> getEmptyClassRooms(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "roomNumber") Pageable pageable) {
        Page<ClassRoom> classRooms = classRoomService.findEmptyRooms(pageable);
        return classRooms.hasContent() 
            ? ResponseEntity.ok(classRooms)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get classrooms without trainers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classrooms without trainers retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No classrooms without trainers found")
    })
    @GetMapping("/without-trainers")
    public ResponseEntity<Page<ClassRoom>> getClassRoomsWithoutTrainers(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "roomNumber") Pageable pageable) {
        Page<ClassRoom> classRooms = classRoomService.findRoomsWithoutTrainers(pageable);
        return classRooms.hasContent() 
            ? ResponseEntity.ok(classRooms)
            : ResponseEntity.noContent().build();
    }
}
