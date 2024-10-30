package com.formation.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.formation.entity.ClassRoom;
import com.formation.entity.Student;
import com.formation.exception.BusinessException;
import com.formation.exception.ResourceNotFoundException;
import com.formation.repository.ClassRoomRepository;
import com.formation.service.impl.ClassRoomServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ClassRoomServiceImplTest {

    @Mock
    private ClassRoomRepository classRoomRepository;

    @InjectMocks
    private ClassRoomServiceImpl classRoomService;

    private ClassRoom classRoom;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        classRoom = ClassRoom.builder()
            .id(1L)
            .name("Test Room")
            .roomNumber("R101")
            .maxCapacity(30)
            .currentCapacity(0)
            .students(new HashSet<>())
            .trainers(new HashSet<>())
            .build();
            
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void save_ShouldCreateClassRoom_WhenValidInput() {
        when(classRoomRepository.existsByRoomNumber(anyString())).thenReturn(false);
        when(classRoomRepository.save(any(ClassRoom.class))).thenReturn(classRoom);

        ClassRoom saved = classRoomService.save(classRoom);

        assertNotNull(saved);
        assertEquals("Test Room", saved.getName());
        verify(classRoomRepository).save(any(ClassRoom.class));
    }

    @Test
    void save_ShouldThrowException_WhenRoomNumberExists() {
        when(classRoomRepository.existsByRoomNumber(anyString())).thenReturn(true);

        assertThrows(BusinessException.class, () -> classRoomService.save(classRoom));
        verify(classRoomRepository, never()).save(any(ClassRoom.class));
    }

    @Test
    void findById_ShouldReturnClassRoom_WhenExists() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(classRoom));

        ClassRoom found = classRoomService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> classRoomService.findById(1L));
    }

    @Test
    void delete_ShouldThrowException_WhenClassRoomHasStudents() {
        classRoom.getStudents().add(new Student());
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(classRoom));

        assertThrows(BusinessException.class, () -> classRoomService.delete(1L));
        verify(classRoomRepository, never()).deleteById(anyLong());
    }

    @Test
    void findAvailableRooms_ShouldReturnAvailableRooms() {
        Page<ClassRoom> page = new PageImpl<>(Arrays.asList(classRoom));
        when(classRoomRepository.findAvailableRooms(anyInt(), any(Pageable.class))).thenReturn(page);

        Page<ClassRoom> result = classRoomService.findAvailableRooms(30, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
} 