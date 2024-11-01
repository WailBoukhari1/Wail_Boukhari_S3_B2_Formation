package com.formation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.formation.entity.Student;
import com.formation.repository.StudentRepository;
import com.formation.service.impl.StudentServiceImpl;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student testStudent;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setFirstName("John");
        testStudent.setLastName("Doe");
        testStudent.setEmail("john.doe@test.com");
        testStudent.setLevel("Intermediate");
        
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void whenSaveStudent_thenReturnSavedStudent() {
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        Student savedStudent = studentService.save(testStudent);

        assertNotNull(savedStudent);
        assertEquals(testStudent.getEmail(), savedStudent.getEmail());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void whenFindById_thenReturnStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        Student found = studentService.findById(1L);

        assertNotNull(found);
        assertEquals(testStudent.getId(), found.getId());
        verify(studentRepository).findById(1L);
    }

    @Test
    void whenFindAll_thenReturnStudentPage() {
        List<Student> students = new ArrayList<>();
        students.add(testStudent);
        Page<Student> studentPage = new PageImpl<>(students);

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<Student> found = studentService.findAll(pageable);

        assertNotNull(found);
        assertEquals(1, found.getTotalElements());
        verify(studentRepository).findAll(pageable);
    }

    @Test
    void whenUpdateStudent_thenReturnUpdatedStudent() {
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        Student updated = studentService.update(testStudent);

        assertNotNull(updated);
        assertEquals(testStudent.getEmail(), updated.getEmail());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void whenDeleteStudent_thenRepositoryMethodCalled() {
        doNothing().when(studentRepository).deleteById(1L);

        studentService.delete(1L);

        verify(studentRepository).deleteById(1L);
    }
}
