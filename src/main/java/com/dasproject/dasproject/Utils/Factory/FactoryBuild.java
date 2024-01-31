package com.dasproject.dasproject.Utils.Factory;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FactoryBuild {
    Project createProject(String name, List<MultipartFile> files);
}
