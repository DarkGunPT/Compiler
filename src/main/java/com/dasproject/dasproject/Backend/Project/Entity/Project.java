package com.dasproject.dasproject.Backend.Project.Entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class Project {
    private String id;
    private String name;
    private List<MultipartFile> files;
    public Project(String name, List<MultipartFile> files){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.files = files;
    }
}


