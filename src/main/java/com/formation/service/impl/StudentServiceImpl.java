package com.formation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.formation.entity.Student;
import com.formation.repository.StudentRepository;
import com.formation.service.StudentService;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public Student save(Student student) {
        validateBusinessRules(student);
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new EntityNotFoundException("Student with email " + student.getEmail() + " already exists");
        }
        return studentRepository.save(student);
    }
    
    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }
    
    @Override
    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }
    
    @Override
    public Student update(Student student) {
        Student existingStudent = findById(student.getId());
        
        if (!existingStudent.getEmail().equals(student.getEmail()) && 
            studentRepository.existsByEmail(student.getEmail())) {
            throw new EntityNotFoundException("Student with email " + student.getEmail() + " already exists");
        }
        
        validateBusinessRules(student);
        return studentRepository.save(student);
    }
    
    @Override
    public void delete(Long id) {
        Student student = findById(id);
        if (student.getCourse() != null) {
            throw new EntityNotFoundException("Cannot delete student enrolled in a course");
        }
        studentRepository.deleteById(id);
    }
    
    @Override
    public Page<Student> search(String keyword, Pageable pageable) {
        return studentRepository.search(keyword, pageable);
    }
    
    @Override
    public Page<Student> findByLevel(String level, Pageable pageable) {
        return studentRepository.findByLevel(level, pageable);
    }
    
    @Override
    public Page<Student> findByCourseId(Long courseId, Pageable pageable) {
        return studentRepository.findByCourseId(courseId, pageable);
    }
    
    @Override
    public Page<Student> findByClassRoomId(Long classRoomId, Pageable pageable) {
        return studentRepository.findByClassRoomId(classRoomId, pageable);
    }
    
    @Override
    public Page<Student> findByLastNameAndFirstName(String lastName, String firstName, Pageable pageable) {
        return studentRepository.findByLastNameAndFirstName(lastName, firstName, pageable);
    }
    
    private void validateBusinessRules(Student student) {
        validateEnrollmentRules(student);
    }

    private void validateEnrollmentRules(Student student) {
        if (student.getCourse() != null && student.getClassRoom() == null) {
            throw new EntityNotFoundException("Student enrolled in a course must be assigned to a classroom");
        }
        if (student.getClassRoom() != null && !student.getClassRoom().isAvailable()) {
            throw new EntityNotFoundException("Selected classroom has reached maximum capacity");
        }
    }
}