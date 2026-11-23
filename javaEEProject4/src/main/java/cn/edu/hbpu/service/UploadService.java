package cn.edu.hbpu.service;

import cn.edu.hbpu.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 图片上传服务：保存到 pic.dir 目录，返回文件名（通过 /pic/{name} 访问）。
 * 位于根容器（SpringConfig 扫描 cn.edu.hbpu.service），供用户头像与图书图片上传共用。
 */
@Service
public class UploadService {

    @Value("${pic.dir}")
    private String picDir;

    /** 单张图片大小上限 2MB */
    private static final long MAX_IMAGE_SIZE = 2L * 1024 * 1024;

    private static final Set<String> ALLOWED_EXT = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp"));

    /**
     * 校验并保存图片，返回生成的文件名
     * @param file   上传文件
     * @param prefix 文件名前缀（如 avatar_1、book）
     */
    public String saveImage(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片文件");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("图片大小不能超过 2MB");
        }
        String original = file.getOriginalFilename();
        String ext = null;
        if (original != null && original.lastIndexOf('.') >= 0) {
            ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        }
        if (ext == null || !ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持 jpg/jpeg/png/gif/webp 格式的图片");
        }
        try {
            byte[] bytes = file.getBytes();
            String actualType = detectType(bytes);
            if (actualType == null || !extensionMatches(ext, actualType)) {
                throw new BusinessException("文件内容与图片扩展名不匹配");
            }
            BufferedImage image;
            try {
                image = ImageIO.read(new ByteArrayInputStream(bytes));
            } catch (IOException imageError) {
                throw new BusinessException("图片内容无效或无法读取");
            }
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                throw new BusinessException("图片内容无效或无法读取");
            }
            String filename = prefix + "_" + System.currentTimeMillis() + "_"
                    + Math.abs(java.util.UUID.randomUUID().hashCode()) + "." + ext;
            File dir = new File(picDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new BusinessException("图片目录不可用");
            }
            file.transferTo(new File(dir, filename));
            return filename;
        } catch (IOException e) {
            throw new BusinessException("图片保存失败");
        }
    }

    private String detectType(byte[] bytes) {
        if (bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50
                && bytes[2] == 0x4e && bytes[3] == 0x47
                && bytes[4] == 0x0d && bytes[5] == 0x0a
                && bytes[6] == 0x1a && bytes[7] == 0x0a) {
            return "png";
        }
        if (bytes.length >= 3 && (bytes[0] & 0xff) == 0xff
                && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff) {
            return "jpg";
        }
        if (bytes.length >= 6 && (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F')
                && bytes[3] == '8' && (bytes[4] == '7' || bytes[4] == '9') && bytes[5] == 'a') {
            return "gif";
        }
        if (bytes.length >= 12 && new String(bytes, 0, 4, StandardCharsets.US_ASCII).equals("RIFF")
                && new String(bytes, 8, 4, StandardCharsets.US_ASCII).equals("WEBP")) {
            return "webp";
        }
        return null;
    }

    private boolean extensionMatches(String ext, String actualType) {
        if ("jpg".equals(actualType)) {
            return "jpg".equals(ext) || "jpeg".equals(ext);
        }
        return actualType.equals(ext);
    }
}
