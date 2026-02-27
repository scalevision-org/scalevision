package com.scalevision.backend.service;

import com.scalevision.backend.dto.CortarVideoRequest;
import com.scalevision.backend.dto.CortarVideoResponse;
import com.scalevision.backend.dto.EstadoVideoResponse;
import com.scalevision.backend.dto.MiniVistasResponse;
import com.scalevision.backend.dto.UploadVideoRequest;
import com.scalevision.backend.dto.UploadVideoResponse;
import com.scalevision.backend.dto.VideoFinalResponse;
import com.scalevision.backend.entity.VideoPoc;
import com.scalevision.backend.entity.VideoStatus;
import com.scalevision.backend.exception.BadRequestException;
import com.scalevision.backend.exception.ResourceNotFoundException;
import com.scalevision.backend.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public UploadVideoResponse subirVideo(UploadVideoRequest request) {
        VideoPoc video = new VideoPoc();
        video.setNombre(request.getNombre());
        video.setNickname(request.getNickname());
        video.setTamano(request.getTamano() == null ? 50.0 : request.getTamano());
        video.setFormato(request.getFormato() == null ? "mp4" : request.getFormato());
        video.setDuracion(request.getDuracion() == null ? 60 : request.getDuracion());
        video.setEstado(VideoStatus.SUBIDO);
        video.setFecha(LocalDateTime.now());
        video.setActivo(true);

        VideoPoc saved = videoRepository.save(video);
        saved.setUrlVideoOriginal(buildOriginalUrl(saved.getId(), saved.getNombre(), saved.getFormato()));
        saved = videoRepository.save(saved);

        return new UploadVideoResponse(saved.getId(), saved.getUrlVideoOriginal(), saved.getEstado().name());
    }

    public EstadoVideoResponse obtenerEstado(Long id) {
        VideoPoc video = findVideoOrThrow(id);
        actualizarFlujoProcesado(video);
        return new EstadoVideoResponse(video.getId(), video.getEstado().name(), video.getError());
    }

    public MiniVistasResponse obtenerMiniVistas(Long id) {
        VideoPoc video = findVideoOrThrow(id);
        actualizarFlujoProcesado(video);

        if (video.getEstado() != VideoStatus.PROCESADO && video.getEstado() != VideoStatus.CORTAR
                && video.getEstado() != VideoStatus.CORTANDO && video.getEstado() != VideoStatus.CORTADO) {
            throw new BadRequestException("El video todavia no tiene mini-vistas disponibles");
        }

        return new MiniVistasResponse(
                video.getId(),
                video.getEstado().name(),
                video.getUrlMiniVista01(),
                video.getUrlMiniVista02(),
                video.getUrlMiniVista03()
        );
    }

    public CortarVideoResponse cortarVideo(Long id, CortarVideoRequest request) {
        VideoPoc video = findVideoOrThrow(id);
        actualizarFlujoProcesado(video);

        if (video.getEstado() != VideoStatus.PROCESADO && video.getEstado() != VideoStatus.CORTAR) {
            throw new BadRequestException("Solo se puede cortar un video en estado PROCESADO");
        }

        video.setUrlVistaSeleccionada(request.getUrlMiniVista());
        video.setEstado(VideoStatus.CORTANDO);
        video.setFecha(LocalDateTime.now());
        videoRepository.save(video);

        return new CortarVideoResponse(video.getId(), video.getEstado().name(), video.getUrlVistaSeleccionada());
    }

    public VideoFinalResponse obtenerVideoFinal(Long id) {
        VideoPoc video = findVideoOrThrow(id);
        actualizarFlujoProcesado(video);
        actualizarFlujoCorte(video);

        if (video.getEstado() != VideoStatus.CORTADO) {
            throw new BadRequestException("El video final aun no esta listo");
        }

        return new VideoFinalResponse(video.getId(), video.getEstado().name(), video.getUrlVideoOriginalCortado());
    }

    private VideoPoc findVideoOrThrow(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el video con id: " + id));
    }

    private void actualizarFlujoProcesado(VideoPoc video) {
        long seconds = ChronoUnit.SECONDS.between(video.getFecha(), LocalDateTime.now());

        if (video.getEstado() == VideoStatus.SUBIDO && seconds >= 2) {
            video.setEstado(VideoStatus.PROCESANDO);
            video.setFecha(LocalDateTime.now());
            videoRepository.save(video);
            return;
        }

        if (video.getEstado() == VideoStatus.PROCESANDO && seconds >= 3) {
            video.setEstado(VideoStatus.PROCESADO);
            video.setFecha(LocalDateTime.now());
            generarMiniVistas(video);
            videoRepository.save(video);
        }
    }

    private void actualizarFlujoCorte(VideoPoc video) {
        long seconds = ChronoUnit.SECONDS.between(video.getFecha(), LocalDateTime.now());

        if (video.getEstado() == VideoStatus.CORTANDO && seconds >= 4) {
            video.setEstado(VideoStatus.CORTADO);
            video.setFecha(LocalDateTime.now());
            video.setUrlVideoOriginalCortado(buildFinalVideoUrl(video.getId()));
            videoRepository.save(video);
        }
    }

    private void generarMiniVistas(VideoPoc video) {
        video.setUrlMiniVista01("https://cdn.scalevision.local/videos/" + video.getId() + "/mini-1.jpg");
        video.setUrlMiniVista02("https://cdn.scalevision.local/videos/" + video.getId() + "/mini-2.jpg");
        video.setUrlMiniVista03("https://cdn.scalevision.local/videos/" + video.getId() + "/mini-3.jpg");
    }

    private String buildOriginalUrl(Long id, String nombre, String formato) {
        String safeNombre = nombre.toLowerCase().replace(" ", "-");
        return "https://cdn.scalevision.local/videos/" + id + "/" + safeNombre + "." + formato;
    }

    private String buildFinalVideoUrl(Long id) {
        return "https://cdn.scalevision.local/videos/" + id + "/final-vertical.mp4";
    }
}
