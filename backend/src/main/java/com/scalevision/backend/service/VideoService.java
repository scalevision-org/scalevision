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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final Path uploadRoot;

    public VideoService(
            VideoRepository videoRepository,
            @Value("${app.storage.upload-dir:uploads}") String uploadDir
    ) {
        this.videoRepository = videoRepository;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo crear el directorio de uploads", ex);
        }
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

    public UploadVideoResponse subirVideoArchivo(MultipartFile videoFile, String nickname, Integer duracion) {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new BadRequestException("Debe enviar un archivo de video");
        }

        String originalName = videoFile.getOriginalFilename() == null ? "video.mp4" : videoFile.getOriginalFilename();
        String formato = extraerExtension(originalName);
        String nombreBase = limpiarNombreSinExtension(originalName);
        String storedFileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + formato;

        Path destination = uploadRoot.resolve(storedFileName);
        try {
            Files.copy(videoFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el archivo de video", ex);
        }

        VideoPoc video = new VideoPoc();
        video.setNombre(nombreBase);
        video.setNickname(nickname);
        video.setTamano(convertirAMegaBytes(videoFile.getSize()));
        video.setFormato(formato);
        video.setDuracion(duracion == null ? 60 : duracion);
        video.setEstado(VideoStatus.SUBIDO);
        video.setFecha(LocalDateTime.now());
        video.setActivo(true);
        video.setRutaArchivoLocal(destination.toString());

        VideoPoc saved = videoRepository.save(video);
        saved.setUrlVideoOriginal("http://localhost:8080/svmvp/uploads/" + storedFileName);
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

    private String extraerExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "mp4";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private String limpiarNombreSinExtension(String fileName) {
        String clean = fileName;
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) {
            clean = fileName.substring(0, lastDot);
        }
        return clean.replace(" ", "-").toLowerCase(Locale.ROOT);
    }

    private double convertirAMegaBytes(long bytes) {
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }
}
