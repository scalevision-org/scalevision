package com.scalevision.backend.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobStatusTest {

    @Test
    void shouldContainAllExpectedStatuses() {
        JobStatus[] values = JobStatus.values();

        assertEquals(4, values.length);
        assertEquals(JobStatus.PENDING, values[0]);
        assertEquals(JobStatus.PROCESSING, values[1]);
        assertEquals(JobStatus.COMPLETED, values[2]);
        assertEquals(JobStatus.FAILED, values[3]);
    }

    @Test
    void shouldResolveStatusWithValueOf() {
        assertEquals(JobStatus.PENDING, JobStatus.valueOf("PENDING"));
        assertEquals(JobStatus.PROCESSING, JobStatus.valueOf("PROCESSING"));
        assertEquals(JobStatus.COMPLETED, JobStatus.valueOf("COMPLETED"));
        assertEquals(JobStatus.FAILED, JobStatus.valueOf("FAILED"));
    }
}
