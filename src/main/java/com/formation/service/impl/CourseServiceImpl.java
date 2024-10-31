package com.formation.service.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formation.entity.Course;
import com.formation.exception.BusinessException;
import com.formation.exception.ResourceNotFoundException;
import com.formation.exception.ValidationException;
import com.formation.repository.CourseRepository;
import com.formation.service.CourseService;
import com.formation.utils.DateUtils;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public Course save(Course course) {
        validateCourse(course);
        return courseRepository.save(course);
    }
    
    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.courseNotFound(id)));
    }
    
    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    
    @Override
    public Course update(Course course) {
        Course existingCourse = findById(course.getId());
        validateCourse(course);
        return courseRepository.save(course);
    }
    
    @Override
    public void delete(Long id) {
        Course course = findById(id);
        if (!course.getStudents().isEmpty()) {
            throw new BusinessException("Cannot delete course with enrolled students");
        }
        courseRepository.deleteById(id);
    }
    
    @Override
    public Page<Course> search(String keyword, Pageable pageable) {
        return courseRepository.search(keyword, pageable);
    }
    
    @Override
    public Page<Course> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date must be before or equal to end date");
        }
        return courseRepository.findByDateRange(startDate, endDate, pageable);
    }
    
    @Override
    public Page<Course> findAvailableCourses(Pageable pageable) {
        return courseRepository.findAvailableCourses(pageable);
    }
    
    @Override
    public Page<Course> findByCapacityRange(int capacity, Pageable pageable) {
        return courseRepository.findByCapacityRange(capacity, pageable);
    }
    
    @Override
    public Page<Course> findUpcomingCourses(Pageable pageable) {
        return courseRepository.findUpcomingCourses(pageable);
    }
    
    @Override
    public Page<Course> findOngoingCourses(Pageable pageable) {
        return courseRepository.findOngoingCourses(pageable);
    }
    
    @Override
    public Page<Course> findByTrainerId(Long trainerId, Pageable pageable) {
        return courseRepository.findByTrainerId(trainerId, pageable);
    }
    
    @Override
    public Page<Course> findCoursesWithoutTrainer(Pageable pageable) {
        return courseRepository.findCoursesWithoutTrainer(pageable);
    }
    
    private void validateCourse(Course course) {
        if (course == null) {
            throw new ValidationException(ValidationException.NULL_COURSE);
        }
        if (!DateUtils.isDateRangeValid(course.getStartDate(), course.getEndDate())) {
            throw new BusinessException(BusinessException.COURSE_DATE_INVALID);
        }
        if (course.getMinCapacity() > course.getMaxCapacity()) {
            throw new BusinessException(BusinessException.COURSE_CAPACITY_RANGE_INVALID);
        }
        if (course.getMaxCapacity() < course.getCurrentCapacity()) {
            throw new BusinessException(BusinessException.CLASSROOM_CAPACITY_INVALID);
        }
    }
}
