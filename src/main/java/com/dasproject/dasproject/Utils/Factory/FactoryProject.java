package com.dasproject.dasproject.Utils.Factory;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FactoryProject implements FactoryBuild {
    @Override
    public Project createProject(String name, List<MultipartFile> files) {
        return  new Project(name,files);
    }
}

