package com.jpmc.midascore.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class FileLoader {
    public String[] loadStrings(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            Path filePath = resource.getFile().toPath();
            List<String> lines = Files.readAllLines(filePath);
            return lines.toArray(new String[0]);
        } catch (IOException e) {
            return null;
        }
    }
}
