package pers.avc.minio.configuration;

import io.minio.MinioClient;
import io.minio.credentials.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import pers.avc.minio.core.MinioObjectOperator;

import java.util.Objects;

/**
 * Minio 客户端自动装配类
 *
 * @author AmVilCresx
 */
@Configuration
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient createMinioClient(
            MinioConfigProperties configProperties,
            @Autowired(required = false) Provider provider) {
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(configProperties.getUrl())
                .credentials(configProperties.getAccessKey(), configProperties.getSecretKey());

        if (StringUtils.hasText(configProperties.getRegion())) {
            builder.region(configProperties.getRegion());
        }

        if (Objects.nonNull(provider)) {
            builder.credentialsProvider(provider);
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnBean(MinioClient.class)
    public MinioObjectOperator minioObjectOperator(MinioClient client) {
        return new MinioObjectOperator(client);
    }
}
