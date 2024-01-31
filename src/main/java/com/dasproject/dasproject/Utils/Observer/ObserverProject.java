package com.dasproject.dasproject.Utils.Observer;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import com.dasproject.dasproject.Utils.STATUS;
import lombok.Data;

import java.util.UUID;
@Data
public class ObserverProject implements ObserverProjectInterface{
    private STATUS status;
    private String projectId;
    public ObserverProject(String projectId) {
        this.projectId = projectId;
    }
    @Override
    public void update(STATUS status, Project project) {
        this.status = status;

        System.out.println(status.getMessage() + " -> " + project.getId() + ": " + project.getName());
    }
}
