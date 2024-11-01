package com.formation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.formation.entity.Trainer;
import com.formation.repository.TrainerRepository;
import com.formation.service.impl.TrainerServiceImpl;

import jakarta.persistence.EntityNotFoundException;

class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setFirstName("John");
        testTrainer.setLastName("Smith");
        testTrainer.setEmail("john.smith@test.com");
        
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void whenSaveTrainer_thenReturnSavedTrainer() {
        when(trainerRepository.existsByEmail(anyString())).thenReturn(false);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer savedTrainer = trainerService.save(testTrainer);

        assertNotNull(savedTrainer);
        assertEquals(testTrainer.getEmail(), savedTrainer.getEmail());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void whenSaveTrainerWithExistingEmail_thenThrowException() {
        when(trainerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> {
            trainerService.save(testTrainer);
        });
    }

    @Test
    void whenFindById_thenReturnTrainer() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));

        Trainer found = trainerService.findById(1L);

        assertNotNull(found);
        assertEquals(testTrainer.getId(), found.getId());
        verify(trainerRepository).findById(1L);
    }

    @Test
    void whenFindByIdNotFound_thenThrowException() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            trainerService.findById(1L);
        });
    }

    @Test
    void whenFindAll_thenReturnTrainerPage() {
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(testTrainer);
        Page<Trainer> trainerPage = new PageImpl<>(trainers);

        when(trainerRepository.findAll(pageable)).thenReturn(trainerPage);

        Page<Trainer> found = trainerService.findAll(pageable);

        assertNotNull(found);
        assertEquals(1, found.getTotalElements());
        verify(trainerRepository).findAll(pageable);
    }

    @Test
    void whenUpdateTrainer_thenReturnUpdatedTrainer() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer updated = trainerService.update(testTrainer);

        assertNotNull(updated);
        assertEquals(testTrainer.getEmail(), updated.getEmail());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void whenDeleteTrainer_thenRepositoryMethodCalled() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));
        doNothing().when(trainerRepository).deleteById(1L);

        trainerService.delete(1L);

        verify(trainerRepository).deleteById(1L);
    }
}
