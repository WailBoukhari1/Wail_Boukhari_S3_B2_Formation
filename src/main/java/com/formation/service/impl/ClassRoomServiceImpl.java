package com.formation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formation.entity.ClassRoom;
import com.formation.exception.BusinessException;
import com.formation.exception.ResourceNotFoundException;
import com.formation.exception.ValidationException;
import com.formation.repository.ClassRoomRepository;
import com.formation.service.ClassRoomService;

@Service
@Transactional
public class ClassRoomServiceImpl implements ClassRoomService {

    @Autowired
    private ClassRoomRepository classRoomRepository;

    @Override
    public ClassRoom save(ClassRoom classRoom) {
        validateClassRoom(classRoom);
        if (classRoomRepository.existsByRoomNumber(classRoom.getRoomNumber())) {
            throw new BusinessException(BusinessException.classroomNumberExists(classRoom.getRoomNumber()));
        }
        return classRoomRepository.save(classRoom);
    }

    private void validateClassRoom(ClassRoom classRoom) {
        if (classRoom == null) {
            throw new ValidationException(ValidationException.NULL_CLASSROOM);
        }
        if (classRoom.getMaxCapacity() < classRoom.getCurrentCapacity()) {
            throw new BusinessException(BusinessException.CLASSROOM_CAPACITY_INVALID);
        }
    }

    @Override
    public ClassRoom findById(Long id) {
        return classRoomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.classroomNotFound(id)));
    }

    @Override
    public Page<ClassRoom> findAll(Pageable pageable) {
        return classRoomRepository.findAll(pageable);
    }

    @Override
    public ClassRoom update(ClassRoom classRoom) {
        ClassRoom existingClassRoom = findById(classRoom.getId());
        
        if (!existingClassRoom.getRoomNumber().equals(classRoom.getRoomNumber()) && 
            classRoomRepository.existsByRoomNumber(classRoom.getRoomNumber())) {
            throw new BusinessException(BusinessException.classroomNumberExists(classRoom.getRoomNumber()));
        }
        
        return classRoomRepository.save(classRoom);
    }

    @Override
    public void delete(Long id) {
        ClassRoom classRoom = findById(id);
        if (!classRoom.getStudents().isEmpty()) {
            throw new BusinessException(BusinessException.CLASSROOM_HAS_STUDENTS);
        }
        if (!classRoom.getTrainers().isEmpty()) {
            throw new BusinessException(BusinessException.CLASSROOM_HAS_TRAINERS);
        }
        classRoomRepository.deleteById(id);
    }

    @Override
    public Page<ClassRoom> search(String keyword, Pageable pageable) {
        return classRoomRepository.search(keyword, pageable);
    }

    @Override
    public Page<ClassRoom> findAvailableRooms(int capacity, Pageable pageable) {
        return classRoomRepository.findAvailableRooms(capacity, pageable);
    }

    @Override
    public Page<ClassRoom> findEmptyRooms(Pageable pageable) {
        return classRoomRepository.findEmptyRooms(pageable);
    }

    @Override
    public Page<ClassRoom> findRoomsWithoutTrainers(Pageable pageable) {
        return classRoomRepository.findRoomsWithoutTrainers(pageable);
    }
}
