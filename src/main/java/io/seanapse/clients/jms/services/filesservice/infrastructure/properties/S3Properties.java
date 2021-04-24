package io.seanapse.clients.jms.services.filesservice.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;
    private String region;
}
