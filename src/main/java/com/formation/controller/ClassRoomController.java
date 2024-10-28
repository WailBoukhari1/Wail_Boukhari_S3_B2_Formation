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

import com.formation.entity.ClassRoom;
import com.formation.service.ClassRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/classrooms")
@Tag(name = "Classroom Management", description = "APIs for managing classrooms")
public class ClassRoomController {

    @Autowired
    private ClassRoomService classRoomService;

    @Operation(summary = "Create a new classroom")
    @ApiResponse(responseCode = "201", description = "Classroom created successfully")
    @PostMapping
    public ResponseEntity<ClassRoom> createClassRoom(@Valid @RequestBody ClassRoom classRoom) {
        return new ResponseEntity<>(classRoomService.save(classRoom), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassRoom> getClassRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(classRoomService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClassRoom>> getAllClassRooms(Pageable pageable) {
        return ResponseEntity.ok(classRoomService.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassRoom> updateClassRoom(
            @PathVariable Long id, 
            @Valid @RequestBody ClassRoom classRoom) {
        classRoom.setId(id);
        return ResponseEntity.ok(classRoomService.update(classRoom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassRoom(@PathVariable Long id) {
        classRoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClassRoom>> searchClassRooms(
            @RequestParam String keyword, 
            Pageable pageable) {
        return ResponseEntity.ok(classRoomService.search(keyword, pageable));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<ClassRoom>> getAvailableRooms(
            @RequestParam int capacity, 
            Pageable pageable) {
        return ResponseEntity.ok(classRoomService.findAvailableRooms(capacity, pageable));
    }

    @GetMapping("/empty")
    public ResponseEntity<Page<ClassRoom>> getEmptyRooms(Pageable pageable) {
        return ResponseEntity.ok(classRoomService.findEmptyRooms(pageable));
    }

    @GetMapping("/without-trainers")
    public ResponseEntity<Page<ClassRoom>> getRoomsWithoutTrainers(Pageable pageable) {
        return ResponseEntity.ok(classRoomService.findRoomsWithoutTrainers(pageable));
    }
}
