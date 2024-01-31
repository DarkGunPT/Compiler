package com.dasproject.dasproject.Utils.Adapter;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface CompilerAdapter {
    String buildProject(MultipartFile[] files, String projectId);
    String runProject(String projectId);
    void removeFiles();
    void copyFile(InputStream source, String destination);
}