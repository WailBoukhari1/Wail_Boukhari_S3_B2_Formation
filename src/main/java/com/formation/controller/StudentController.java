package com.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.formation.entity.Student;
import com.formation.entity.Course;
import com.formation.service.CourseService;
import com.formation.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "APIs for managing students")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
    RequestMethod.DELETE, RequestMethod.OPTIONS})
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;

    @Operation(summary = "Create a new student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully",
            content = @Content(schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation failed"),
        @ApiResponse(responseCode = "409", description = "Student with same email already exists")
    })
    @PostMapping
    public ResponseEntity<Student> createStudent(
            @Parameter(description = "Student to create", required = true) 
            @RequestBody(required = true) Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        try {
            return new ResponseEntity<>(studentService.save(student), HttpStatus.CREATED);
        } catch (Exception e) {
            if (e.getMessage().contains("email already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Email " + student.getEmail() + " already exists");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Failed to create student: " + e.getMessage());
        }
    }

    @Operation(summary = "Get a student by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "ID of the student") 
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.findById(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Student not found with id: " + id);
        }
    }

    @Operation(summary = "Get all students with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found")
    })
    @GetMapping
    public ResponseEntity<Page<Student>> getAllStudents(
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName", direction = Sort.Direction.ASC) 
            Pageable pageable) {
        Page<Student> students = studentService.findAll(pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student updated successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @Parameter(description = "Student ID") 
            @PathVariable Long id,
            @Parameter(description = "Updated student details") 
            @RequestBody Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        try {
            student.setId(id);
            return ResponseEntity.ok(studentService.update(student));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Student not found with id: " + id);
            } else if (e.getMessage().contains("email already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Email " + student.getEmail() + " already exists");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Failed to update student: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete a student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "400", description = "Student cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Student ID") 
            @PathVariable Long id) {
        try {
            studentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("enrolled in a course")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot delete student who is enrolled in a course");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Student not found with id: " + id);
        }
    }

    @Operation(summary = "Search students by keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No matching students found")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<Student>> searchStudents(
            @Parameter(description = "Search keyword") 
            @RequestParam String keyword,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        if (keyword.trim().length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        Page<Student> students = studentService.search(keyword, pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get students by level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found for this level"),
        @ApiResponse(responseCode = "400", description = "Invalid level")
    })
    @GetMapping("/level/{level}")
    public ResponseEntity<Page<Student>> getStudentsByLevel(
            @Parameter(description = "Student level") 
            @PathVariable String level,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Student> students = studentService.findByLevel(level, pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get students by course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found in this course")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<Student>> getStudentsByCourse(
            @Parameter(description = "Course ID") 
            @PathVariable Long courseId,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        try {
            Page<Student> students = studentService.findByCourseId(courseId, pageable);
            return students.hasContent() 
                ? ResponseEntity.ok(students)
                : ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Course not found with id: " + courseId);
        }
    }

    @Operation(summary = "Get students by classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found in this classroom")
    })
    @GetMapping("/classroom/{classRoomId}")
    public ResponseEntity<Page<Student>> getStudentsByClassRoom(
            @Parameter(description = "Classroom ID") 
            @PathVariable Long classRoomId,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        try {
            Page<Student> students = studentService.findByClassRoomId(classRoomId, pageable);
            return students.hasContent() 
                ? ResponseEntity.ok(students)
                : ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Classroom not found with id: " + classRoomId);
        }
    }

    @Operation(summary = "Get students by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found with these names")
    })
    @GetMapping("/name")
    public ResponseEntity<Page<Student>> getStudentsByName(
            @Parameter(description = "Student's last name") 
            @RequestParam String lastName,
            @Parameter(description = "Student's first name") 
            @RequestParam String firstName,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Student> students = studentService.findByLastNameAndFirstName(lastName, firstName, pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get courses by date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No courses found for this date range"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    @GetMapping("/date-range")
    public ResponseEntity<Page<Course>> getCoursesByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true) 
            @RequestParam LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)", required = true) 
            @RequestParam LocalDate endDate,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        Page<Course> courses = courseService.findByDateRange(startDate, endDate, pageable);
        return courses.hasContent() 
            ? ResponseEntity.ok(courses)
            : ResponseEntity.noContent().build();
    }
}