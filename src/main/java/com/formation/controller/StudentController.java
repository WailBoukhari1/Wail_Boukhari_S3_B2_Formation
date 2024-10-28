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

import com.formation.entity.Student;
import com.formation.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "Create a new student")
    @ApiResponse(responseCode = "201", description = "Student created successfully")
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        return new ResponseEntity<>(studentService.save(student), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Student>> getAllStudents(Pageable pageable) {
        return ResponseEntity.ok(studentService.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id, 
            @Valid @RequestBody Student student) {
        student.setId(id);
        return ResponseEntity.ok(studentService.update(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Student>> searchStudents(
            @RequestParam String keyword, 
            Pageable pageable) {
        return ResponseEntity.ok(studentService.search(keyword, pageable));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<Page<Student>> getStudentsByLevel(
            @PathVariable String level, 
            Pageable pageable) {
        return ResponseEntity.ok(studentService.findByLevel(level, pageable));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<Student>> getStudentsByCourse(
            @PathVariable Long courseId, 
            Pageable pageable) {
        return ResponseEntity.ok(studentService.findByCourseId(courseId, pageable));
    }

    @GetMapping("/classroom/{classRoomId}")
    public ResponseEntity<Page<Student>> getStudentsByClassRoom(
            @PathVariable Long classRoomId, 
            Pageable pageable) {
        return ResponseEntity.ok(studentService.findByClassRoomId(classRoomId, pageable));
    }

    @GetMapping("/name")
    public ResponseEntity<Page<Student>> getStudentsByName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            Pageable pageable) {
        return ResponseEntity.ok(studentService.findByLastNameAndFirstName(lastName, firstName, pageable));
    }
}
