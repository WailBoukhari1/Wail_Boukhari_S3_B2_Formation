package com.formation.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.entity.Course;
import com.formation.service.CourseService;

@WebMvcTest(CourseController.class)
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");
        testCourse.setStartDate(LocalDate.now());
        testCourse.setEndDate(LocalDate.now().plusDays(30));
        testCourse.setMaxCapacity(20);
        testCourse.setCurrentCapacity(0);
    }

    @Test
    void whenCreateCourse_thenReturn201() throws Exception {
        when(courseService.save(any(Course.class))).thenReturn(testCourse);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCourse)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(testCourse.getTitle()));
    }

    @Test
    void whenGetCourseById_thenReturnCourse() throws Exception {
        when(courseService.findById(1L)).thenReturn(testCourse);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value(testCourse.getTitle()));
    }

    @Test
    void whenGetAllCourses_thenReturnCoursePage() throws Exception {
        List<Course> courses = new ArrayList<>();
        courses.add(testCourse);
        Page<Course> coursePage = new PageImpl<>(courses);

        when(courseService.findAll(any(Pageable.class))).thenReturn(coursePage);

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(testCourse.getTitle()));
    }

    @Test
    void whenUpdateCourse_thenReturnUpdatedCourse() throws Exception {
        when(courseService.update(any(Course.class))).thenReturn(testCourse);

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testCourse.getTitle()));
    }

    @Test
    void whenDeleteCourse_thenReturn204() throws Exception {
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }
}
