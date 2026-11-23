package cn.edu.hbpu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * 图片静态服务：/pic/{文件名} -> 本机 picture 目录。
 * 不用 Spring 的 ResourceHttpRequestHandler：tomcat7 插件内嵌 Tomcat 7 仅支持 Servlet 3.0，
 * 而 Spring 5.3 资源处理会调用 Servlet 3.1 的 setContentLengthLong 导致 NoSuchMethodError。
 * 这里直接用 Servlet 3.0 API 流式输出图片。
 */
@RestController
public class PicController {

    @Value("${pic.dir}")
    private String picDir;

    @GetMapping("/pic/{name:.+}")
    public void pic(@PathVariable String name, HttpServletResponse response) throws IOException {
        // 只允许平铺文件名，防止路径穿越
        if (name.contains("..") || name.contains("/") || name.contains("\\")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(picDir, name);
        if (!file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentType = URLConnection.guessContentTypeFromName(name);
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setContentLength((int) file.length());
        Files.copy(file.toPath(), response.getOutputStream());
    }
}