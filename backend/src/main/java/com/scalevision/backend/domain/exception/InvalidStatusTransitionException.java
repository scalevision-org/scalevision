package com.scalevision.backend.domain.exception;

import com.scalevision.backend.domain.model.JobStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(JobStatus currentStatus, JobStatus nextStatus) {
        super("Invalid status transition: " + currentStatus + " -> " + nextStatus);
    }
}
