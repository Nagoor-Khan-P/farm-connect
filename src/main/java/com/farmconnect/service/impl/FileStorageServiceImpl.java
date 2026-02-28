package com.farmconnect.service.impl;

import com.farmconnect.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root = Paths.get("uploads");

    public FileStorageServiceImpl() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String saveFile(MultipartFile file, String subDirectory) {
        try {
            Path subPath = root.resolve(subDirectory);
            if (!Files.exists(subPath)) {
                Files.createDirectories(subPath);
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), subPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDirectory + "/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}
