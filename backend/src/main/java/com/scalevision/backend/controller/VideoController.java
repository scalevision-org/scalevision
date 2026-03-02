package com.scalevision.backend.controller;

import com.scalevision.backend.dto.CortarVideoRequest;
import com.scalevision.backend.dto.CortarVideoResponse;
import com.scalevision.backend.dto.EstadoVideoResponse;
import com.scalevision.backend.dto.MiniVistasResponse;
import com.scalevision.backend.dto.UploadVideoRequest;
import com.scalevision.backend.dto.UploadVideoResponse;
import com.scalevision.backend.dto.VideoFinalResponse;
import com.scalevision.backend.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/subir", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadVideoResponse> subirVideo(@Valid @RequestBody UploadVideoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoService.subirVideo(request));
    }

    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadVideoResponse> subirVideoArchivo(
            @RequestParam("video") MultipartFile video,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "duracion", required = false) Integer duracion
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoService.subirVideoArchivo(video, nickname, duracion));
    }

    @GetMapping("/estado/{id}")
    public ResponseEntity<EstadoVideoResponse> obtenerEstado(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.obtenerEstado(id));
    }

    @GetMapping("/mini-vistas/{id}")
    public ResponseEntity<MiniVistasResponse> obtenerMiniVistas(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.obtenerMiniVistas(id));
    }

    @PostMapping("/cortar-video/{id}")
    public ResponseEntity<CortarVideoResponse> cortarVideo(
            @PathVariable Long id,
            @Valid @RequestBody CortarVideoRequest request
    ) {
        return ResponseEntity.ok(videoService.cortarVideo(id, request));
    }

    @GetMapping("/final/{id}")
    public ResponseEntity<VideoFinalResponse> obtenerFinal(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.obtenerVideoFinal(id));
    }
}
