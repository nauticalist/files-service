package io.seanapse.clients.jms.services.filesservice.file.api.server;

import io.seanapse.clients.jms.services.filesservice.file.domain.model.FileInfo;
import io.seanapse.clients.jms.services.filesservice.file.dto.PagedFileResponse;
import io.seanapse.clients.jms.services.filesservice.file.exception.ResourceNotFoundException;
import io.seanapse.clients.jms.services.filesservice.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/files")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_FILES_MANAGER')")
    public ResponseEntity<List<FileInfo>> uploadFiles(@RequestParam(value = "files") List<MultipartFile> files,
                                                      @AuthenticationPrincipal Jwt principal) {
        String userId = (String) principal.getClaims().get("sub");
        return ResponseEntity.ok(fileService.saveFiles(files, userId));
    }

    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('ROLE_FILES_MANAGER')")
    public ResponseEntity<String> removeFile(@PathVariable(value = "fileId") String fileId,
                                             @AuthenticationPrincipal Jwt principal) throws ResourceNotFoundException {
        String userId = (String) principal.getClaims().get("sub");
        fileService.removeFile(fileId, userId);
        return ResponseEntity.ok(fileId);
    }

    @GetMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('ROLE_FILES_USER')")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable(value = "fileId") String fileId) throws ResourceNotFoundException {
        return ResponseEntity.ok(fileService.getFileInfo(fileId));
    }

    @GetMapping("/download/{fileId}")
    @PreAuthorize("hasAnyRole('ROLE_FILES_USER')")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable(value = "fileId") String fileId) throws ResourceNotFoundException {
        final byte[] data = fileService.getFile(fileId);
        final ByteArrayResource resource = new ByteArrayResource(data);
        var fileInfo = fileService.getFileInfo(fileId);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentLength(data.length);
        header.set("Content-Disposition", "attachment; filename=" + fileInfo.getFileName());

        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }

    @GetMapping("/filter/{filter}")
    @PreAuthorize("hasAnyRole('ROLE_FILES_USER')")
    public ResponseEntity<List<FileInfo>> searchFileInfos(@PathVariable(value = "filter") String filter) {
        List<FileInfo> files = fileService.searchFiles(filter);

        if(files == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_FILES_USER')")
    public ResponseEntity<PagedFileResponse> getFileInfosByPage(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                @RequestParam(name = "size", defaultValue = "20") int size) {
        PagedFileResponse files = fileService.getFilesByPage(page, size);

        if(files.getFiles() == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
