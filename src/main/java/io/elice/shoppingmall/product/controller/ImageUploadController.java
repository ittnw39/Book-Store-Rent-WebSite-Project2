package io.elice.shoppingmall.product.controller;

import io.elice.shoppingmall.product.service.S3Service;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageUploadController {

    private final S3Service s3Service;

    @Autowired
    public ImageUploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("fileName") String fileName) {
        try {
            String url = s3Service.upload(file, fileName);
            return ResponseEntity.ok("File upload success. URL: " + url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload fail. Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            boolean fileExists = s3Service.doesFileExist(fileName);
            if (!fileExists) {
                return ResponseEntity.ok("File does not exist.");
            }

            s3Service.delete(fileName);
            return ResponseEntity.ok("File delete success.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File delete fail. Error: " + e.getMessage());
        }
    }
}
