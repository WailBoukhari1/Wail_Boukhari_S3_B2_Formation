package com.formation.exception;

public class ValidationException extends BaseException {
    public static final String NULL_REQUEST_BODY = "Request body cannot be null";
    public static final String NULL_CLASSROOM = "Classroom cannot be null";
    public static final String NULL_TRAINER = "Trainer cannot be null";
    public static final String NULL_STUDENT = "Student cannot be null";
    public static final String NULL_COURSE = "Course cannot be null";

    public ValidationException(String message) {
        super(message);
    }
}