package io.elice.shoppingmall.product.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    //파일 업로드
    public String upload(MultipartFile multipartFile, String s3FileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    //파일 존재 확인
    public boolean doesFileExist(String s3FileName) {
        return amazonS3.doesObjectExist(bucket, s3FileName);
    }

    //파일 삭제
    public void delete(String s3FileName) {
        amazonS3.deleteObject(bucket, s3FileName);
    }

    //book 엔티티 imageurl에서 이미지 파일명 추출하는 메서드
    public String extractObjectKeyFromUrl(String url) {
        try {
            URL s3Url = new URL(url);
            System.out.println(s3Url.getPath().substring(1));
            return s3Url.getPath().substring(1);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid S3 URL", e);
        }
    }
}
