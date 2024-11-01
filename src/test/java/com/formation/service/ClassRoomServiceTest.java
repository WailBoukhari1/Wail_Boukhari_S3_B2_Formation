package com.formation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.formation.entity.ClassRoom;
import com.formation.repository.ClassRoomRepository;
import com.formation.service.impl.ClassRoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;

class ClassRoomServiceTest {

    @Mock
    private ClassRoomRepository classRoomRepository;

    @InjectMocks
    private ClassRoomServiceImpl classRoomService;

    private ClassRoom testClassRoom;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testClassRoom = new ClassRoom();
        testClassRoom.setId(1L);
        testClassRoom.setName("Test Room");
        testClassRoom.setRoomNumber("TR-101");
        testClassRoom.setMaxCapacity(30);
        testClassRoom.setCurrentCapacity(0);
        
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void whenSaveClassRoom_thenReturnSavedClassRoom() {
        when(classRoomRepository.existsByRoomNumber(anyString())).thenReturn(false);
        when(classRoomRepository.save(any(ClassRoom.class))).thenReturn(testClassRoom);

        ClassRoom savedClassRoom = classRoomService.save(testClassRoom);

        assertNotNull(savedClassRoom);
        assertEquals(testClassRoom.getRoomNumber(), savedClassRoom.getRoomNumber());
        verify(classRoomRepository).save(any(ClassRoom.class));
    }

    @Test
    void whenSaveClassRoomWithExistingNumber_thenThrowException() {
        when(classRoomRepository.existsByRoomNumber(anyString())).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> {
            classRoomService.save(testClassRoom);
        });
    }

    @Test
    void whenFindById_thenReturnClassRoom() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(testClassRoom));

        ClassRoom found = classRoomService.findById(1L);

        assertNotNull(found);
        assertEquals(testClassRoom.getId(), found.getId());
        verify(classRoomRepository).findById(1L);
    }

    @Test
    void whenFindByIdNotFound_thenThrowException() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classRoomService.findById(1L);
        });
    }

    @Test
    void whenUpdateClassRoom_thenReturnUpdatedClassRoom() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(testClassRoom));
        when(classRoomRepository.save(any(ClassRoom.class))).thenReturn(testClassRoom);

        ClassRoom updated = classRoomService.update(testClassRoom);

        assertNotNull(updated);
        assertEquals(testClassRoom.getRoomNumber(), updated.getRoomNumber());
        verify(classRoomRepository).save(any(ClassRoom.class));
    }

    @Test
    void whenDeleteClassRoom_thenRepositoryMethodCalled() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(testClassRoom));
        doNothing().when(classRoomRepository).deleteById(1L);

        classRoomService.delete(1L);

        verify(classRoomRepository).deleteById(1L);
    }
}
