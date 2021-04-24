package io.seanapse.clients.jms.services.filesservice.file.api.client.impl;

import io.seanapse.clients.jms.services.filesservice.file.api.client.S3Service;
import io.seanapse.clients.jms.services.filesservice.infrastructure.properties.S3Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;


@Service
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Autowired
    public S3ServiceImpl(S3Client s3Client, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    @Override
    @Async
    public void uploadFile(String fileName, File file) {
        final PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromFile(file));
    }

    @Override
    @Async
    public void uploadFileWithPublicAccessPermissions(String fileName, File file) {
        final PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromFile(file));
    }

    @Override
    @Async
    public void deleteFile(String fileName) {
        final DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .build();

        s3Client.deleteObject(objectRequest);
    }

    @Override
    public byte[] downloadFile(String fileName) {
        log.info("[S3 Service] request initialized for file " + fileName);
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .build();

        ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(objectRequest);

        byte[] fileData = null;

        try {
            fileData = IOUtils.toByteArray(stream);
            log.info("File retrieved successfully");
            stream.close();
        } catch (IOException e) {
            log.info("Error occured while retrieving file: " + e.getMessage());
        }

        return fileData;
    }
}
