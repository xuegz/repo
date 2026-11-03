package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 通用图片上传（平台端、商户端、小程序共用）
 * 配置了 OSS 时上传到阿里云，否则回退本地磁盘
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadController {

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".png", "image/png",
            ".gif", "image/gif",
            ".webp", "image/webp",
            ".bmp", "image/bmp");

    private final OssService ossService;

    @Value("${jiancai.upload.path}")
    private String uploadPath;

    @Value("${jiancai.upload.url-prefix}")
    private String urlPrefix;

    @Value("${jiancai.oss.dir-prefix:jiancai}")
    private String dirPrefix;

    @PostMapping({"/api/admin/upload", "/api/merchant/upload", "/api/mp/upload"})
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail(400, "文件为空");
        }
        String ext = extension(file.getOriginalFilename());
        if (!CONTENT_TYPES.containsKey(ext)) {
            return R.fail(400, "仅支持 jpg/png/gif/webp/bmp 图片");
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        Map<String, String> data = new HashMap<>(4);
        if (ossService.isEnabled()) {
            String objectKey = objectKey(filename);
            try (InputStream in = file.getInputStream()) {
                ossService.upload(objectKey, in, file.getSize(), CONTENT_TYPES.get(ext));
            } catch (Exception e) {
                log.error("OSS 上传失败: {}", objectKey, e);
                return R.fail(500, "图片上传失败，请稍后重试");
            }
            data.put("url", ossService.url(objectKey));
        } else {
            Path dir = Paths.get(uploadPath);
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename).toFile());
            data.put("url", urlPrefix + filename);
        }
        data.put("filename", filename);
        return R.ok("上传成功", data);
    }

    private String objectKey(String filename) {
        String prefix = dirPrefix == null ? "" : dirPrefix.trim().replaceAll("^/+|/+$", "");
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        return (prefix.isEmpty() ? "" : prefix + "/") + month + "/" + filename;
    }

    private String extension(String name) {
        if (name == null) {
            return "";
        }
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot).toLowerCase(Locale.ROOT);
    }
}
