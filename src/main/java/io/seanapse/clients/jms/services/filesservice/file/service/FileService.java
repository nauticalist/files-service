package io.seanapse.clients.jms.services.filesservice.file.service;

import io.seanapse.clients.jms.services.filesservice.file.domain.model.FileInfo;
import io.seanapse.clients.jms.services.filesservice.file.dto.PagedFileResponse;
import io.seanapse.clients.jms.services.filesservice.file.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileInfo> saveFiles(List<MultipartFile> files, String userId);

    FileInfo saveFile(MultipartFile multipartFile, String userId);

    void removeFile(String fileId, String userId) throws ResourceNotFoundException;

    byte[] getFile(final String fileId) throws ResourceNotFoundException;

    FileInfo getFileInfo(final String fileId) throws ResourceNotFoundException;

    List<FileInfo> searchFiles(String filter);

    PagedFileResponse getFilesByPage(int page, int size);
}
