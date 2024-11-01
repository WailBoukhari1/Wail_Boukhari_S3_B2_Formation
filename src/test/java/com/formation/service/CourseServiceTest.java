package com.formation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.formation.entity.Course;
import com.formation.repository.CourseRepository;
import com.formation.service.impl.CourseServiceImpl;
import jakarta.persistence.EntityNotFoundException;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course testCourse;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");
        testCourse.setStartDate(LocalDate.now());
        testCourse.setEndDate(LocalDate.now().plusDays(30));
        testCourse.setMaxCapacity(20);
        testCourse.setCurrentCapacity(0);
        
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void whenSaveCourse_thenReturnSavedCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course savedCourse = courseService.save(testCourse);

        assertNotNull(savedCourse);
        assertEquals(testCourse.getTitle(), savedCourse.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void whenFindById_thenReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        Course found = courseService.findById(1L);

        assertNotNull(found);
        assertEquals(testCourse.getId(), found.getId());
        verify(courseRepository).findById(1L);
    }

    @Test
    void whenFindByIdNotFound_thenThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.findById(1L);
        });
    }

    @Test
    void whenFindAll_thenReturnCoursePage() {
        List<Course> courses = new ArrayList<>();
        courses.add(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses);

        when(courseRepository.findAll(pageable)).thenReturn(coursePage);

        Page<Course> found = courseService.findAll(pageable);

        assertNotNull(found);
        assertEquals(1, found.getTotalElements());
        verify(courseRepository).findAll(pageable);
    }

    @Test
    void whenUpdateCourse_thenReturnUpdatedCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course updated = courseService.update(testCourse);

        assertNotNull(updated);
        assertEquals(testCourse.getTitle(), updated.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void whenDeleteCourse_thenRepositoryMethodCalled() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        doNothing().when(courseRepository).deleteById(1L);

        courseService.delete(1L);

        verify(courseRepository).deleteById(1L);
    }
}
