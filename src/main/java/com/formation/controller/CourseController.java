package com.formation.controller;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Management", description = "APIs for managing training courses")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
    RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Create a new training course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully",
            content = @Content(schema = @Schema(implementation = Course.class))),
        @ApiResponse(responseCode = "400", description = "Invalid course data or validation failed"),
        @ApiResponse(responseCode = "409", description = "Course with same title already exists")
    })
    @PostMapping
    public ResponseEntity<Course> createCourse(
            @Parameter(description = "Course details", required = true) 
            @Valid @RequestBody(required = true) Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        try {
            return new ResponseEntity<>(courseService.save(course), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Course with title '" + course.getTitle() + "' already exists");
        }
    }

    @Operation(summary = "Get course details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course details retrieved"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(
            @Parameter(description = "Course ID") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        try {
            return ResponseEntity.ok(courseService.findById(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Course not found with id: " + id);
        }
    }

    @Operation(summary = "Get all courses with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of courses retrieved"),
        @ApiResponse(responseCode = "204", description = "No courses found")
    })
    @GetMapping
    public ResponseEntity<Page<Course>> getAllCourses(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<Course> courses = courseService.findAll(pageable);
        return courses.hasContent() 
            ? ResponseEntity.ok(courses)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update course details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "400", description = "Invalid course data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @Parameter(description = "Course ID") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id,
            @Parameter(description = "Updated course details") 
            @Valid @RequestBody(required = true) Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        try {
            course.setId(id);
            return ResponseEntity.ok(courseService.update(course));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Course not found with id: " + id);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Course with title '" + course.getTitle() + "' already exists");
        }
    }

    @Operation(summary = "Delete a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "400", description = "Course cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        try {
            courseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("in progress")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot delete a course that is in progress");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Course not found with id: " + id);
        }
    }

    @Operation(summary = "Get courses by date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No courses found in date range"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    @GetMapping("/date-range")
    public ResponseEntity<Page<Course>> getCoursesByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true) 
            @RequestParam @NotNull(message = "Start date is required") LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)", required = true) 
            @RequestParam @NotNull(message = "End date is required") LocalDate endDate,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        Page<Course> courses = courseService.findByDateRange(startDate, endDate, pageable);
        return courses.hasContent() ? ResponseEntity.ok(courses) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search courses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved"),
        @ApiResponse(responseCode = "204", description = "No matching courses found")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<Course>> searchCourses(
            @Parameter(description = "Search keyword") 
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") String keyword,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        if (keyword.trim().length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        Page<Course> courses = courseService.search(keyword, pageable);
        return courses.hasContent() ? ResponseEntity.ok(courses) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get courses by trainer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved"),
        @ApiResponse(responseCode = "204", description = "No courses found for trainer")
    })
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<Page<Course>> getCoursesByTrainer(
            @Parameter(description = "Trainer ID") 
            @PathVariable @Min(value = 1, message = "Trainer ID must be positive") Long trainerId,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        try {
            Page<Course> courses = courseService.findByTrainerId(trainerId, pageable);
            return courses.hasContent() ? ResponseEntity.ok(courses) : ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Trainer not found with id: " + trainerId);
        }
    }
}