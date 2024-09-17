package go.alarm.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import go.alarm.global.config.S3Config;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final AmazonS3 amazonS3;

    private final S3Config s3Config;


    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata(); // 추가적인 정보를 담아줌
        metadata.setContentType("image/jpeg");
        metadata.setContentLength(file.getSize());

        log.warn("metadata(imageUploader) >> " + metadata);

        try {
            amazonS3.putObject(new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));
        }catch (IOException e){
            log.error("error at ImageUploader uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    }

    public String generateUserProfileKeyName(String uuid) {
        return s3Config.getUserProfilePath() + '/' + uuid;
    }


}