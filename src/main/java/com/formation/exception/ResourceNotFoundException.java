package com.formation.exception;

public class ResourceNotFoundException extends BaseException {
    public static final String TRAINER_NOT_FOUND = "Trainer not found with id: %d";
    public static final String STUDENT_NOT_FOUND = "Student not found with id: %d";
    public static final String CLASSROOM_NOT_FOUND = "Classroom not found with id: %d";
    public static final String COURSE_NOT_FOUND = "Course not found with id: %d";

    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Utility methods for formatted messages
    public static String trainerNotFound(Long id) {
        return String.format(TRAINER_NOT_FOUND, id);
    }

    public static String studentNotFound(Long id) {
        return String.format(STUDENT_NOT_FOUND, id);
    }

    public static String classroomNotFound(Long id) {
        return String.format(CLASSROOM_NOT_FOUND, id);
    }

    public static String courseNotFound(Long id) {
        return String.format(COURSE_NOT_FOUND, id);
    }
}