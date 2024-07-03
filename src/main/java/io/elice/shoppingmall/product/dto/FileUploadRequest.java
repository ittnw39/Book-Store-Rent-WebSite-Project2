package io.elice.shoppingmall.product.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {

    private MultipartFile file;
    private String fileName;
}
