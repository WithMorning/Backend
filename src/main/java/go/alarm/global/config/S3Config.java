package go.alarm.global.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class S3Config {


    private AWSCredentials awsCredentials;

    private String accessKey = "${aws.credentials.accessKey}";

    private String secretKey = "${aws.credentials.secretKey}";

    private String region = "${aws.region.static}";

    private String bucket = "${aws.s3.bucket}";

    private String userProfilePath = "${aws.s3.path}";


    @PostConstruct
    public void init() { // 빈 생성 후 자동으로 실행되어 awsCredentials를 초기화
        // AWS 자격 증명을 저장
        this.awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3 amazonS3() { // S3 서비스와 상호작용하는 데 사용
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey); // AWS 자격 증명 객체 생성
        return AmazonS3ClientBuilder.standard()
            .withRegion(region) // 리전 설정
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            // 자격 증명 제공자 설정
            // AWSStaticCredentialsProvider는 정적인(변하지 않는) 자격 증명을 제공
            .build();
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() { // AWS 자격 증명 제공자를 생성
        return new AWSStaticCredentialsProvider(awsCredentials);
    }
}