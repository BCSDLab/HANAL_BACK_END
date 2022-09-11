package com.bcsdlab.biseo.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3Util {

    // jpg, png, gif, bmp

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public String uploadFile(String savedName, MultipartFile file) throws IOException {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType(file.getContentType());
        omd.setContentLength(file.getSize());
        amazonS3.putObject(bucket, savedName, file.getInputStream(), omd);

        return amazonS3.getUrl(bucket, savedName).toString();
    }

    public void deleteFile(String savedName) {
        amazonS3.deleteObject(bucket, savedName);
    }
}
