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

import com.formation.entity.Student;
import com.formation.service.StudentService;

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
@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "APIs for managing students")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
    RequestMethod.DELETE, RequestMethod.OPTIONS})
public class StudentController {

    @Autowired
    private StudentService studentService;

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
            @Valid @RequestBody(required = true) Student student) {
        if (student == null) {
            throw new ValidationException("Request body cannot be null");
        }
        return new ResponseEntity<>(studentService.save(student), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a student by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "ID of the student") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        return ResponseEntity.ok(studentService.findById(id));
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
            @Parameter(description = "ID of the student to update") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id,
            @Parameter(description = "Updated student details") 
            @Valid @RequestBody(required = true) Student student) {
        if (student == null) {
            throw new ValidationException("Request body cannot be null");
        }
        student.setId(id);
        return ResponseEntity.ok(studentService.update(student));
    }

    @Operation(summary = "Delete a student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "400", description = "Student cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "ID of the student to delete") 
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search students by keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No matching students found")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<Student>> searchStudents(
            @Parameter(description = "Search keyword") 
            @RequestParam @NotBlank(message = "Search keyword cannot be empty") String keyword,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
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
            @PathVariable @NotBlank(message = "Level cannot be empty") String level,
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
            @PathVariable @Min(value = 1, message = "Course ID must be positive") Long courseId,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Student> students = studentService.findByCourseId(courseId, pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get students by classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found in this classroom")
    })
    @GetMapping("/classroom/{classRoomId}")
    public ResponseEntity<Page<Student>> getStudentsByClassRoom(
            @Parameter(description = "Classroom ID") 
            @PathVariable @Min(value = 1, message = "Classroom ID must be positive") Long classRoomId,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Student> students = studentService.findByClassRoomId(classRoomId, pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get students by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No students found with these names")
    })
    @GetMapping("/name")
    public ResponseEntity<Page<Student>> getStudentsByName(
            @Parameter(description = "Student's last name") 
            @RequestParam @NotBlank(message = "Last name cannot be empty") String lastName,
            @Parameter(description = "Student's first name") 
            @RequestParam @NotBlank(message = "First name cannot be empty") String firstName,
            @Parameter(description = "Pagination parameters") 
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        Page<Student> students = studentService.findByLastNameAndFirstName(lastName, firstName, pageable);
        return students.hasContent() 
            ? ResponseEntity.ok(students)
            : ResponseEntity.noContent().build();
    }
}