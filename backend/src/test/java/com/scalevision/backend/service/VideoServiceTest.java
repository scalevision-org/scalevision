package com.scalevision.backend.service;

import com.scalevision.backend.dto.UploadVideoRequest;
import com.scalevision.backend.dto.UploadVideoResponse;
import com.scalevision.backend.entity.VideoStatus;
import com.scalevision.backend.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class VideoServiceTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void subirVideo_debeGuardarVideoConEstadoSubido() {
        UploadVideoRequest request = new UploadVideoRequest();
        request.setNombre("demo-video");
        request.setTamano(50.0);

        UploadVideoResponse response = videoService.subirVideo(request);

        assertNotNull(response.getId());
        assertEquals(VideoStatus.SUBIDO.name(), response.getEstado());
        assertNotNull(videoRepository.findById(response.getId()).orElseThrow().getUrlVideoOriginal());
    }
}
