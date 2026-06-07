package com.example.employee.service.attachment;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {

    String storeFile(MultipartFile file, Long employeeId, String storedFileName);

    InputStream getFileAsStream(String filePath);

    byte[] getFileAsBytes(String filePath);

    boolean deleteFile(String filePath);

    long getFileSize(String filePath);
}
