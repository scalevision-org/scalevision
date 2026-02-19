package com.scalevision.backend.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VideoMetadataTest {
    @Test
    void deberiaCrearVideoMetadataValido() {
        VideoMetadata metadata = new VideoMetadata(120, "1920x1080", "mp4", 1048576L);
        assertEquals(120, metadata.duration());
        assertEquals("1920x1080", metadata.resolution());
        assertEquals("mp4", metadata.format());
        assertEquals(1048576L, metadata.fileSize());
    }

    @Test
    void deberiaFallarConDuracionCero() {
        assertThrows(IllegalArgumentException.class, () ->
                new VideoMetadata(0, "1920x1080", "mp4", 1048576L));
    }

    @Test
    void deberiaFallarConDuracionNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
                new VideoMetadata(-1, "1920x1080", "mp4", 1048576L));
    }

    @Test
    void deberiaFallarConResolucionInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
                new VideoMetadata(120, "fullhd", "mp4", 1048576L));
    }

    @Test
    void deberiaFallarConFormatoVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                new VideoMetadata(120, "1920x1080", "", 1048576L));
    }

    @Test
    void deberiaFallarConFileSizeCero() {
        assertThrows(IllegalArgumentException.class, () ->
                new VideoMetadata(120, "1920x1080", "mp4", 0L));
    }

    @Test
    void deberiaFallarConFileSizeNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
                new VideoMetadata(120, "1920x1080", "mp4", -1L));
    }
}
