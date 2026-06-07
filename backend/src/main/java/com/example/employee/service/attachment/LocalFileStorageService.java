package com.example.employee.service.attachment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${attachment.storage.path:./attachments}")
    private String storageBasePath;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageBasePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long employeeId, String storedFileName) {
        String employeeDir = "employee_" + employeeId;
        Path employeePath = this.rootLocation.resolve(employeeDir);
        try {
            Files.createDirectories(employeePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create employee storage directory", e);
        }

        String filename = StringUtils.cleanPath(storedFileName);
        try {
            if (filename.contains("..")) {
                throw new RuntimeException("Invalid path sequence in filename: " + filename);
            }
            Path targetLocation = employeePath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return employeeDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    @Override
    public InputStream getFileAsStream(String filePath) {
        try {
            Path file = this.rootLocation.resolve(filePath).normalize();
            if (!file.startsWith(this.rootLocation)) {
                throw new RuntimeException("Invalid file path");
            }
            return new FileInputStream(file.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }
    }

    @Override
    public byte[] getFileAsBytes(String filePath) {
        try {
            Path file = this.rootLocation.resolve(filePath).normalize();
            if (!file.startsWith(this.rootLocation)) {
                throw new RuntimeException("Invalid file path");
            }
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path file = this.rootLocation.resolve(filePath).normalize();
            if (!file.startsWith(this.rootLocation)) {
                throw new RuntimeException("Invalid file path");
            }
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public long getFileSize(String filePath) {
        try {
            Path file = this.rootLocation.resolve(filePath).normalize();
            if (!file.startsWith(this.rootLocation)) {
                throw new RuntimeException("Invalid file path");
            }
            return Files.size(file);
        } catch (IOException e) {
            return 0;
        }
    }
}
