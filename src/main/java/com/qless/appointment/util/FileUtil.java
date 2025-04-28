package com.qless.appointment.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static void saveFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String path = System.getProperty("user.home") + "/appointments/uploads/";
        Path dir = Paths.get(path);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        Path filePath = dir.resolve(fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(multipartFile.getBytes());
        }
    }

    public static boolean checkHeader(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file.getBytes())))) {
            String header = reader.readLine();
            if (header != null) {
                String[] headers = header.split(",");
                return headers.length == 2
                        && headers[0].equals("external_id")
                        && headers[1].equals("appointment_time");
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
