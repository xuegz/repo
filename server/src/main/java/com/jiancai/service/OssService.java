package com.jiancai.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 阿里云 OSS 上传；未配置凭据时由调用方回退本地磁盘
 */
@Slf4j
@Service
public class OssService {

    @Value("${jiancai.oss.endpoint:}")
    private String endpoint;

    @Value("${jiancai.oss.bucket:}")
    private String bucket;

    @Value("${jiancai.oss.access-key-id:}")
    private String accessKeyId;

    @Value("${jiancai.oss.access-key-secret:}")
    private String accessKeySecret;

    @Value("${jiancai.oss.public-domain:}")
    private String publicDomain;

    private volatile OSS client;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            log.info("图片上传：阿里云 OSS（bucket={}, endpoint={}）", bucket, endpoint);
        } else {
            log.info("图片上传：本地磁盘（未配置 OSS 凭据）");
        }
    }

    public boolean isEnabled() {
        return StringUtils.hasText(endpoint) && StringUtils.hasText(bucket)
                && StringUtils.hasText(accessKeyId) && StringUtils.hasText(accessKeySecret);
    }

    public void upload(String objectKey, InputStream in, long size, String contentType) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(size);
        if (StringUtils.hasText(contentType)) {
            meta.setContentType(contentType);
        }
        getClient().putObject(bucket, objectKey, in, meta);
    }

    public String url(String objectKey) {
        String host;
        if (StringUtils.hasText(publicDomain)) {
            host = publicDomain.trim();
            if (!host.startsWith("http://") && !host.startsWith("https://")) {
                host = "https://" + host;
            }
        } else {
            String ep = endpoint.trim().replaceFirst("^https?://", "");
            host = "https://" + bucket.trim() + "." + ep;
        }
        return host.replaceAll("/+$", "") + "/" + objectKey;
    }

    private OSS getClient() {
        OSS c = client;
        if (c == null) {
            synchronized (this) {
                if (client == null) {
                    client = new OSSClientBuilder().build(endpoint.trim(), accessKeyId.trim(), accessKeySecret.trim());
                    log.info("OSS 图片上传已启用：bucket={}, endpoint={}", bucket, endpoint);
                }
                c = client;
            }
        }
        return c;
    }

    @PreDestroy
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }
}
