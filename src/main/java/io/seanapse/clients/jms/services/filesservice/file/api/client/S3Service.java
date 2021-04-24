package io.seanapse.clients.jms.services.filesservice.file.api.client;

import java.io.File;

public interface S3Service {
    void uploadFile(String fileName, File file);
    void uploadFileWithPublicAccessPermissions(String fileName, File file);
    void deleteFile(String fileName);
    byte[] downloadFile(String fileName);
}
