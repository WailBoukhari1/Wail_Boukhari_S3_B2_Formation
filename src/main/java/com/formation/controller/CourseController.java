package com.formation.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.entity.Course;
import com.formation.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")  
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Create a new course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully",
            content = @Content(schema = @Schema(implementation = Course.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Course> createCourse(
            @Parameter(description = "Course to create") @Valid @RequestBody Course course) {
        return new ResponseEntity<>(courseService.save(course), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a course by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(
            @Parameter(description = "ID of the course") @PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @Operation(summary = "Get all courses with pagination")
    @ApiResponse(responseCode = "200", description = "List of courses retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<Course>> getAllCourses(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(courseService.findAll(pageable));
    }

    @Operation(summary = "Update a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @Parameter(description = "ID of the course to update") @PathVariable Long id,
            @Parameter(description = "Updated course details") @Valid @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(courseService.update(course));
    }

    @Operation(summary = "Delete a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "ID of the course to delete") @PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search courses by keyword")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<Page<Course>> searchCourses(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(courseService.search(keyword, pageable));
    }

    @Operation(summary = "Get courses by date range")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    @GetMapping("/date-range")
    public ResponseEntity<Page<Course>> getCoursesByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam LocalDate endDate,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(courseService.findByDateRange(startDate, endDate, pageable));
    }

    @Operation(summary = "Get available courses")
    @ApiResponse(responseCode = "200", description = "Available courses retrieved successfully")
    @GetMapping("/available")
    public ResponseEntity<Page<Course>> getAvailableCourses(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(courseService.findAvailableCourses(pageable));
    }
}
