package com.scalevision.backend.service;

import com.scalevision.backend.dto.CortarVideoRequest;
import com.scalevision.backend.dto.CortarVideoResponse;
import com.scalevision.backend.dto.EstadoVideoResponse;
import com.scalevision.backend.dto.MiniVistasResponse;
import com.scalevision.backend.dto.UploadVideoRequest;
import com.scalevision.backend.dto.UploadVideoResponse;
import com.scalevision.backend.dto.VideoFinalResponse;
import com.scalevision.backend.entity.ModoCorte;
import com.scalevision.backend.entity.VideoPoc;
import com.scalevision.backend.entity.VideoStatus;
import com.scalevision.backend.exception.BadRequestException;
import com.scalevision.backend.exception.ResourceNotFoundException;
import com.scalevision.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final RestTemplate restTemplate;

    private final Path storageRoot;
    private final Path originalsDir;
    private final Path finalsDir;
    private final Path thumbnailsDir;

    private final String aiBaseUrl;
    private final String aiLocalProcessingDir;

    public VideoService(
            VideoRepository videoRepository,
            @Value("${app.storage.upload-dir:uploads}") String uploadDir,
            @Value("${app.ai.base-url:http://localhost:8000}") String aiBaseUrl,
            @Value("${app.ai.local-processing-dir:}") String aiLocalProcessingDir
    ) {
        this.videoRepository = videoRepository;
        this.restTemplate = new RestTemplate();
        this.aiBaseUrl = aiBaseUrl;
        this.aiLocalProcessingDir = aiLocalProcessingDir;

        this.storageRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.originalsDir = storageRoot.resolve("originals");
        this.finalsDir = storageRoot.resolve("finals");
        this.thumbnailsDir = storageRoot.resolve("thumbnails");
        try {
            Files.createDirectories(this.storageRoot);
            Files.createDirectories(this.originalsDir);
            Files.createDirectories(this.finalsDir);
            Files.createDirectories(this.thumbnailsDir);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo crear el directorio de uploads", ex);
        }
    }

    public UploadVideoResponse subirVideoArchivo(
            MultipartFile videoFile,
            String modoCorteRaw,
            String nombre,
            Double tamano,
            String formato
    ) {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new BadRequestException("Debe enviar un archivo de video");
        }
        ModoCorte modoCorte = ModoCorte.fromValue(modoCorteRaw);

        String originalName = videoFile.getOriginalFilename() == null ? "video.mp4" : videoFile.getOriginalFilename();
        String formatoArchivo = extraerExtension(originalName);
        String nombreBase = limpiarNombreSinExtension(originalName);
        String formatoFinal = (formato == null || formato.isBlank()) ? formatoArchivo : formato.toLowerCase(Locale.ROOT);
        String nombreFinal = (nombre == null || nombre.isBlank()) ? nombreBase : nombre.trim();
        double tamanoFinal = tamano == null ? convertirAMegaBytes(videoFile.getSize()) : tamano;

        String storedFileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + formatoArchivo;
        Path destination = originalsDir.resolve(storedFileName);

        try {
            Files.copy(videoFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            replicarArchivoParaIa(storedFileName, destination);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el archivo de video", ex);
        }

        VideoPoc video = new VideoPoc();
        video.setNombre(nombreFinal);
        video.setNickname(null);
        video.setTamano(tamanoFinal);
        video.setFormato(formatoFinal);
        video.setDuracion(60);
        video.setModoCorte(modoCorte.getValue());
        video.setEstado(VideoStatus.SUBIDO);
        video.setFecha(LocalDateTime.now());
        video.setActivo(true);
        video.setRutaArchivoLocal(destination.toString());
        video.setIaJobId(UUID.randomUUID().toString());

        VideoPoc saved = videoRepository.save(video);
        saved.setUrlVideoOriginal("http://localhost:8080/svmvp/uploads/originals/" + storedFileName);
        iniciarScanEnIa(saved);
        saved = videoRepository.save(saved);

        return new UploadVideoResponse(saved.getId(), saved.getUrlVideoOriginal(), saved.getEstado().name());
    }

    public UploadVideoResponse subirVideo(UploadVideoRequest request) {
        VideoPoc video = new VideoPoc();
        video.setNombre(request.getNombre());
        video.setNickname(request.getNickname());
        video.setTamano(request.getTamano() == null ? 50.0 : request.getTamano());
        video.setFormato(request.getFormato() == null ? "mp4" : request.getFormato());
        video.setDuracion(request.getDuracion() == null ? 60 : request.getDuracion());
        video.setModoCorte(ModoCorte.CENTER_CROP.getValue());
        video.setEstado(VideoStatus.SUBIDO);
        video.setFecha(LocalDateTime.now());
        video.setActivo(true);
        video.setIaJobId(UUID.randomUUID().toString());

        VideoPoc saved = videoRepository.save(video);
        saved.setUrlVideoOriginal("http://localhost:8080/svmvp/uploads/originals/" + saved.getId() + "-demo.mp4");
        saved = videoRepository.save(saved);

        return new UploadVideoResponse(saved.getId(), saved.getUrlVideoOriginal(), saved.getEstado().name());
    }

    public EstadoVideoResponse obtenerEstado(Long id) {
        VideoPoc video = findVideoOrThrow(id);
        refrescarScanDesdeIa(video);
        return new EstadoVideoResponse(video.getId(), video.getEstado().name(), video.getError());
    }

    public MiniVistasResponse obtenerMiniVistas(Long id) {
        VideoPoc video = findVideoOrThrow(id);
        Map<String, Object> scanBody = refrescarScanDesdeIa(video);

        if (video.getEstado() != VideoStatus.PROCESADO && video.getEstado() != VideoStatus.CORTANDO
                && video.getEstado() != VideoStatus.CORTADO) {
            throw new BadRequestException("El video todavia no tiene mini-vistas disponibles");
        }

        if (scanBody != null) {
            aplicarMiniVistasDesdeScan(video, scanBody);
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
        Map<String, Object> scanBody = refrescarScanDesdeIa(video);

        if (video.getEstado() != VideoStatus.PROCESADO) {
            throw new BadRequestException("Solo se puede cortar un video en estado PROCESADO");
        }

        String strategy = video.getModoCorte();
        String targetSubjectId = null;

        if (ModoCorte.FACE_TRACKING.getValue().equals(strategy)) {
            targetSubjectId = encontrarTargetSubjectId(scanBody, request.getUrlMiniVista());
            if (targetSubjectId == null) {
                throw new BadRequestException("No se encontro subject_id para la mini-vista seleccionada");
            }
        }

        lanzarProcesoEnIa(video.getIaJobId(), strategy, targetSubjectId);

        video.setUrlVistaSeleccionada(request.getUrlMiniVista());
        video.setEstado(VideoStatus.CORTANDO);
        video.setFecha(LocalDateTime.now());
        videoRepository.save(video);

        return new CortarVideoResponse(video.getId(), video.getEstado().name(), video.getUrlVistaSeleccionada());
    }

    public VideoFinalResponse obtenerVideoFinal(Long id) {
        VideoPoc video = findVideoOrThrow(id);
        refrescarProcessDesdeIa(video);

        if (video.getEstado() != VideoStatus.CORTADO) {
            throw new BadRequestException("El video final aun no esta listo");
        }

        return new VideoFinalResponse(video.getId(), video.getEstado().name(), video.getUrlVideoOriginalCortado());
    }

    private VideoPoc findVideoOrThrow(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el video con id: " + id));
    }

    private void iniciarScanEnIa(VideoPoc video) {
        try {
            Map<String, Object> payload = Map.of(
                    "id", video.getIaJobId(),
                    "video_url", video.getUrlVideoOriginal(),
                    "modo_corte", video.getModoCorte()
            );
            restTemplate.postForEntity(aiBaseUrl + "/scan", new HttpEntity<>(payload), Map.class);
            video.setEstado(VideoStatus.SUBIDO);
        } catch (HttpStatusCodeException ex) {
            video.setEstado(VideoStatus.ERROR);
            video.setError(extraerMensajeErrorIa(ex));
            throw new BadRequestException("IA rechazo el escaneo: " + extraerMensajeErrorIa(ex));
        } catch (Exception ex) {
            video.setEstado(VideoStatus.ERROR);
            video.setError("No se pudo conectar con IA en /scan");
            throw new IllegalStateException("No se pudo conectar con IA en /scan", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> refrescarScanDesdeIa(VideoPoc video) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(aiBaseUrl + "/scan/" + video.getIaJobId(), Map.class);
            Map<String, Object> body = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK) {
                video.setEstado(VideoStatus.PROCESADO);
                video.setError(null);
                aplicarMiniVistasDesdeScan(video, body);
            }

            videoRepository.save(video);
            return body;
        } catch (HttpStatusCodeException ex) {
            int status = ex.getStatusCode().value();
            if (status == 202) {
                video.setEstado(VideoStatus.PROCESANDO);
                video.setError(null);
                videoRepository.save(video);
                return null;
            }

            video.setEstado(VideoStatus.ERROR);
            video.setError(extraerMensajeErrorIa(ex));
            videoRepository.save(video);
            throw new BadRequestException("Error IA en scan: " + extraerMensajeErrorIa(ex));
        }
    }

    @SuppressWarnings("unchecked")
    private void lanzarProcesoEnIa(String iaJobId, String strategy, String targetSubjectId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", iaJobId);
            payload.put("strategy", strategy);
            payload.put("target_subject_id", targetSubjectId);
            restTemplate.postForEntity(aiBaseUrl + "/process-video", new HttpEntity<>(payload), Map.class);
        } catch (HttpStatusCodeException ex) {
            throw new BadRequestException("IA rechazo process-video: " + extraerMensajeErrorIa(ex));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo conectar con IA en /process-video", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void refrescarProcessDesdeIa(VideoPoc video) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(aiBaseUrl + "/process-video/" + video.getIaJobId(), Map.class);
            Map<String, Object> body = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK) {
                video.setEstado(VideoStatus.CORTADO);
                video.setError(null);

                String outputVideoUrl = extraerOutputVideoUrl(body);
                if (outputVideoUrl == null || outputVideoUrl.isBlank()) {
                    throw new BadRequestException("IA no devolvio output_video_url");
                }
                video.setUrlVideoOriginalCortado(outputVideoUrl);
            }

            videoRepository.save(video);
        } catch (HttpStatusCodeException ex) {
            int status = ex.getStatusCode().value();
            if (status == 202) {
                video.setEstado(VideoStatus.CORTANDO);
                video.setError(null);
                videoRepository.save(video);
                return;
            }

            video.setEstado(VideoStatus.ERROR);
            video.setError(extraerMensajeErrorIa(ex));
            videoRepository.save(video);
            throw new BadRequestException("Error IA en process-video: " + extraerMensajeErrorIa(ex));
        }
    }

    @SuppressWarnings("unchecked")
    private void aplicarMiniVistasDesdeScan(VideoPoc video, Map<String, Object> scanBody) {
        if (scanBody == null) {
            return;
        }
        Object subjectsObj = scanBody.get("subjects");
        if (!(subjectsObj instanceof List<?> subjects) || subjects.isEmpty()) {
            return;
        }

        String mini1 = null;
        String mini2 = null;
        String mini3 = null;

        int count = 0;
        for (Object item : subjects) {
            if (!(item instanceof Map<?, ?> m)) {
                continue;
            }
            String thumbnail = extraerThumbnailUrl((Map<String, Object>) m);
            if (thumbnail == null) {
                continue;
            }
            count++;
            if (count == 1) {
                mini1 = thumbnail;
            } else if (count == 2) {
                mini2 = thumbnail;
            } else if (count == 3) {
                mini3 = thumbnail;
                break;
            }
        }

        video.setUrlMiniVista01(mini1);
        video.setUrlMiniVista02(mini2);
        video.setUrlMiniVista03(mini3);
    }

    @SuppressWarnings("unchecked")
    private String encontrarTargetSubjectId(Map<String, Object> scanBody, String selectedUrl) {
        if (scanBody == null) {
            return null;
        }
        Object subjectsObj = scanBody.get("subjects");
        if (!(subjectsObj instanceof List<?> subjects)) {
            return null;
        }

        for (Object item : subjects) {
            if (!(item instanceof Map<?, ?> rawMap)) {
                continue;
            }
            Map<String, Object> subject = (Map<String, Object>) rawMap;
            String thumbnail = extraerThumbnailUrl(subject);
            if (thumbnail != null && thumbnail.equals(selectedUrl)) {
                Object subjectId = subject.get("subject_id");
                return subjectId == null ? null : subjectId.toString();
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String extraerThumbnailUrl(Map<String, Object> subject) {
        Object thumbnail = subject.get("thumbnail_url");
        if (thumbnail instanceof String s) {
            return s;
        }
        if (thumbnail instanceof Map<?, ?> m) {
            Object format = ((Map<String, Object>) m).get("format");
            if (format instanceof String s) {
                return s;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extraerOutputVideoUrl(Map<String, Object> body) {
        if (body == null) {
            return null;
        }
        Object output = body.get("output_video_url");
        if (output instanceof String s) {
            return s;
        }
        if (output instanceof Map<?, ?> m) {
            Object format = ((Map<String, Object>) m).get("format");
            if (format instanceof String s) {
                return s;
            }
        }
        return null;
    }

    private String extraerMensajeErrorIa(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return "status=" + ex.getStatusCode().value();
        }
        return body;
    }

    private void replicarArchivoParaIa(String fileName, Path source) throws IOException {
        if (aiLocalProcessingDir == null || aiLocalProcessingDir.isBlank()) {
            return;
        }
        Path iaDir = Paths.get(aiLocalProcessingDir).toAbsolutePath().normalize();
        Files.createDirectories(iaDir);
        Path iaCopy = iaDir.resolve(fileName);
        Files.copy(source, iaCopy, StandardCopyOption.REPLACE_EXISTING);
    }

    private String extraerExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "mp4";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private String limpiarNombreSinExtension(String fileName) {
        String clean = fileName;
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            clean = fileName.substring(0, lastDot);
        }
        return clean.replace(" ", "-").toLowerCase(Locale.ROOT);
    }

    private double convertirAMegaBytes(long bytes) {
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }
}
