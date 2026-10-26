package com.jiancai.controller;

import com.jiancai.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 通用图片上传（平台端、商户端共用）
 */
@RestController
public class UploadController {

    @Value("${jiancai.upload.path}")
    private String uploadPath;

    @Value("${jiancai.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping({"/api/admin/upload", "/api/merchant/upload"})
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail(400, "文件为空");
        }
        Path dir = Paths.get(uploadPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        Map<String, String> data = new HashMap<>(4);
        data.put("url", urlPrefix + filename);
        data.put("filename", filename);
        return R.ok("上传成功", data);
    }
}
