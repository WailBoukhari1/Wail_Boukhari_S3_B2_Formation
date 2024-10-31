package com.formation.exception;

public class BusinessException extends BaseException {
    public static final String TRAINER_EMAIL_EXISTS = "A trainer with email %s already exists";
    public static final String STUDENT_EMAIL_EXISTS = "A student with email %s already exists";
    public static final String TRAINER_HAS_COURSES = "Cannot delete trainer with assigned courses";
    public static final String CLASSROOM_NUMBER_EXISTS = "A classroom with room number %s already exists";
    public static final String CLASSROOM_CAPACITY_INVALID = "Maximum capacity cannot be less than current capacity";
    public static final String COURSE_TITLE_EXISTS = "A course with title %s already exists";
    public static final String COURSE_DATE_INVALID = "Start date must be before end date";
    public static final String COURSE_CAPACITY_RANGE_INVALID = "Minimum capacity must be less than or equal to maximum capacity";
    public static final String CLASSROOM_HAS_STUDENTS = "Cannot delete classroom with enrolled students";
    public static final String CLASSROOM_HAS_TRAINERS = "Cannot delete classroom with assigned trainers";

    public BusinessException(String message) {
        super(message);
    }

    // Utility methods for formatted messages
    public static String trainerEmailExists(String email) {
        return String.format(TRAINER_EMAIL_EXISTS, email);
    }

    public static String studentEmailExists(String email) {
        return String.format(STUDENT_EMAIL_EXISTS, email);
    }

    public static String classroomNumberExists(String roomNumber) {
        return String.format(CLASSROOM_NUMBER_EXISTS, roomNumber);
    }
}