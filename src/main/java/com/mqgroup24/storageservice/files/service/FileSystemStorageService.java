package com.mqgroup24.storageservice.files.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileSystemStorageService implements StorageSevice {

    @Value("${media.location}")
    private String mediaLocation;

    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() throws IOException {
        rootLocation = Paths.get(mediaLocation);
        Files.createDirectories(rootLocation);
    }

    @Override
    public String store(MultipartFile file) {

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            String fileName = file.getOriginalFilename();
            assert fileName != null;
            Path destinationFile = rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();


            try(InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path filePath = rootLocation.resolve(Paths.get(filename));
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
