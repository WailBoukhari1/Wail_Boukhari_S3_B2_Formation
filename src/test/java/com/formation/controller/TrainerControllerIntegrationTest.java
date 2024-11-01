package com.formation.controller;

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
import com.formation.entity.Trainer;
import com.formation.service.TrainerService;

@WebMvcTest(TrainerController.class)
class TrainerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setFirstName("John");
        testTrainer.setLastName("Smith");
        testTrainer.setEmail("john.smith@test.com");
    }

    @Test
    void whenCreateTrainer_thenReturn201() throws Exception {
        when(trainerService.save(any(Trainer.class))).thenReturn(testTrainer);

        mockMvc.perform(post("/api/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTrainer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(testTrainer.getEmail()));
    }

    @Test
    void whenGetTrainerById_thenReturnTrainer() throws Exception {
        when(trainerService.findById(1L)).thenReturn(testTrainer);

        mockMvc.perform(get("/api/trainers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(testTrainer.getEmail()));
    }

    @Test
    void whenGetAllTrainers_thenReturnTrainerPage() throws Exception {
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(testTrainer);
        Page<Trainer> trainerPage = new PageImpl<>(trainers);

        when(trainerService.findAll(any(Pageable.class))).thenReturn(trainerPage);

        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value(testTrainer.getEmail()));
    }

    @Test
    void whenUpdateTrainer_thenReturnUpdatedTrainer() throws Exception {
        when(trainerService.update(any(Trainer.class))).thenReturn(testTrainer);

        mockMvc.perform(put("/api/trainers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTrainer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testTrainer.getEmail()));
    }

    @Test
    void whenDeleteTrainer_thenReturn204() throws Exception {
        mockMvc.perform(delete("/api/trainers/1"))
                .andExpect(status().isNoContent());
    }
}
