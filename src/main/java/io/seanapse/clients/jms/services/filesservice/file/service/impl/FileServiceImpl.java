package io.seanapse.clients.jms.services.filesservice.file.service.impl;

import io.seanapse.clients.jms.services.filesservice.file.api.client.S3Service;
import io.seanapse.clients.jms.services.filesservice.file.domain.model.FileInfo;
import io.seanapse.clients.jms.services.filesservice.file.domain.repository.FileInfoRepository;
import io.seanapse.clients.jms.services.filesservice.file.dto.PagedFileResponse;
import io.seanapse.clients.jms.services.filesservice.file.exception.FileConversionException;
import io.seanapse.clients.jms.services.filesservice.file.exception.InvalidFileTypeException;
import io.seanapse.clients.jms.services.filesservice.file.exception.ResourceNotFoundException;
import io.seanapse.clients.jms.services.filesservice.file.service.FileService;
import io.seanapse.clients.jms.services.filesservice.infrastructure.properties.S3Properties;
import io.seanapse.clients.jms.services.filesservice.infrastructure.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final S3Service s3service;
    private final FileInfoRepository fileInfoRepository;
    private final S3Properties properties;

    @Autowired
    public FileServiceImpl(S3Service s3service, FileInfoRepository fileInfoRepository, S3Properties properties) {
        this.s3service = s3service;
        this.fileInfoRepository = fileInfoRepository;
        this.properties = properties;
    }

    @Override
    public List<FileInfo> saveFiles(List<MultipartFile> files, String userId) {
        List<FileInfo> fileInfos = new ArrayList<>();
        files.forEach(file -> fileInfos.add(this.saveFile(file, userId)));
        return fileInfos;
    }

    @Override
    @Transactional
    public FileInfo saveFile(MultipartFile multipartFile, String userId) {
        List<String> validExtensions = Arrays.asList("pdf", "doc", "docx", "odt", "xls", "xlsx", "ods","txt", "jpeg", "jpg", "png");
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        if (!validExtensions.contains(extension)) {
            // If file have a invalid extension, call an Exception.
            log.warn("Invalid file type detected. Cancelling request");
            throw new InvalidFileTypeException(validExtensions);
        } else {
            String newFileName = saveFileToS3(multipartFile);
            log.info("New File saved with name" + newFileName);
            FileInfo fileInfo = FileInfo.builder()
                    .fileId(UUID.randomUUID().toString())
                    .fileName(newFileName)
                    .fileUrl(properties.getEndPoint().concat(newFileName))
                    .createdAt(new Date())
                    .createdBy(userId)
                    .build();
            log.info("New file saved to the database with info: " + fileInfo);
            return fileInfoRepository.save(fileInfo);
        }
    }

    /**
     * Sets deleted field as true. Because of retention policy files will not be removed
     * @param fileId
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void removeFile(String fileId, String userId) throws ResourceNotFoundException {
        FileInfo file = this.getFileInfoFromDB(fileId);
        log.debug("Remove file request in progress for file: " + file.toString());
        file.setDeleted(true);
        file.setModifiedAt(new Date());
        file.setModifiedBy(userId);
        fileInfoRepository.save(file);
    }

    @Override
    public byte[] getFile(String fileId) throws ResourceNotFoundException {
        FileInfo fileInfo = this.getFileInfoFromDB(fileId);
        log.info("[File Service] Download file request for : " + fileInfo.toString());
        return s3service.downloadFile(fileInfo.getFileName());
    }

    @Override
    @Transactional(readOnly = true)
    public FileInfo getFileInfo(String fileId) throws ResourceNotFoundException {
        return this.getFileInfoFromDB(fileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileInfo> searchFiles(String filter) {
        return fileInfoRepository.findByFilterRegex(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedFileResponse getFilesByPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var totalNumberOfFiles = fileInfoRepository.count();
        var files  = new ArrayList<>(fileInfoRepository.findFileInfosByDeletedFalseAndPage(pageRequest));

        return new PagedFileResponse(pageRequest, files, totalNumberOfFiles);
    }


    private String saveFileToS3(MultipartFile multipartFile) {
        String fileName;
        try {
            File file = FileUtils.convertMultipartToFile(multipartFile);
            fileName = FileUtils.generateFileName(multipartFile);
            s3service.uploadFile(fileName, file);
            file.delete();
        } catch (IOException e) {
            log.warn("File conversion failed with error :: " + e.getMessage());
            throw new FileConversionException();
        }
        return fileName;
    }

    private FileInfo getFileInfoFromDB(String fileId) throws ResourceNotFoundException {
        return fileInfoRepository.findByFileId(fileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("File not found :: %s", fileId)
                ));
    }
}
