package com.mqgroup24.storageservice.files.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageSevice {

    void init() throws IOException;

    String store(MultipartFile file);

    Resource load(String filename);

}
