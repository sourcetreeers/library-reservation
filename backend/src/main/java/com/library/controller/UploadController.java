package com.library.controller;

import com.library.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return Result.error(401, "未登录");
        }

        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名无效");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".png".equals(ext) && !".gif".equals(ext)) {
            return Result.error("仅支持 JPG/PNG/GIF 格式的图片");
        }

        try {
            // 生成唯一文件名
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 保存目录：项目根目录下的 uploads/complaints/
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "complaints";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(uploadDir, newFileName);
            file.transferTo(dest);

            // 返回相对URL路径
            String url = "/uploads/complaints/" + newFileName;
            log.info("文件上传成功：{}", url);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：", e);
            return Result.error("文件上传失败");
        }
    }
}
