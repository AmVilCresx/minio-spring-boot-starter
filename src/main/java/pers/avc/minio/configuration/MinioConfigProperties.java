package pers.avc.minio.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Minio 客户端配置类
 *
 * @author AmVilCresx
 */
@ConfigurationProperties(prefix = MinioConfigProperties.MINIO_PREFIX)
public class MinioConfigProperties {

    public static final String MINIO_PREFIX = "minio";

    private String url;

    private String accessKey;

    private String secretKey;

    private String region;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
