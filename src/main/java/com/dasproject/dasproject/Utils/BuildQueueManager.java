package com.dasproject.dasproject.Utils;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import com.dasproject.dasproject.Backend.Project.ProjectService.ProjectService;
import com.dasproject.dasproject.Utils.Factory.FactoryBuild;
import com.dasproject.dasproject.Utils.Factory.FactoryProject;
import com.dasproject.dasproject.Utils.Observer.ObserverProject;
import com.dasproject.dasproject.Utils.Observer.ObserverProjectManager;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;


public class BuildQueueManager {
    private static BuildQueueManager instance;
    private ProjectService projectService;
    private FactoryBuild projectFactory;
    private static ObserverProjectManager observerProjectManager;
    @Getter
    private List<Project> projects;


    private BuildQueueManager() {
        projectService = new ProjectService();
        observerProjectManager = new ObserverProjectManager();
        projectFactory = new FactoryProject();
        this.projects = new ArrayList<>();
    }

    public static synchronized BuildQueueManager getInstance() {
        if (instance == null) {
            instance = new BuildQueueManager();
        }

        return instance;
    }

    public ResponseEntity<String> enqueueBuild(MultipartFile[] build, String projectName) {
        Project newProject = projectFactory.createProject(projectName, Arrays.stream(build).toList());
        projects.add(newProject);

        ObserverProject observer = new ObserverProject(newProject.getId());
        observerProjectManager.addObserver(observer);

        showQueue();

        observerProjectManager.dataChanged(STATUS.WAITING, observerProjectManager.getObserverByProject(newProject.getId()), newProject);

        return processNextBuild();
    }

    private ResponseEntity<String> processNextBuild() {
        Project projectToCompile = getNextProjectToCompile();

        ResponseEntity<String> output = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Á espera de poder compilar...");

        observerProjectManager.dataChanged(STATUS.BUILDING, observerProjectManager.getObserverByProject(projectToCompile.getId()), projectToCompile);

        if (projectToCompile.getFiles() == null || projectToCompile.getFiles().isEmpty()) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("output", "O projeto não tem ficheiros.");
            output = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse.toString());

            String responseBody = output.getBody();
            jsonResponse = new JSONObject(responseBody);
            String outputValue = jsonResponse.getString("output");
            System.out.println("Output: " + outputValue);

            buildCompleted(projectToCompile);

            return output;
        }

        MultipartFile[] nextBuild = projectToCompile.getFiles().toArray(new MultipartFile[0]);

        output = projectService.buildCode(nextBuild, projectToCompile.getId());

        switch (output.getStatusCode().value()) {
            case 200 ->
                    observerProjectManager.dataChanged(STATUS.BUILDING_SUCCESS, observerProjectManager.getObserverByProject(projectToCompile.getId()), projectToCompile);
            case 400 -> {
                observerProjectManager.dataChanged(STATUS.BUILDING_ERROR, observerProjectManager.getObserverByProject(projectToCompile.getId()), projectToCompile);

                String responseBody = output.getBody();
                JSONObject jsonResponse = new JSONObject(responseBody);
                String outputValue = jsonResponse.getString("output");
                System.out.println("Output: " + outputValue);

                buildCompleted(projectToCompile);

                return output;
            }
        }

        observerProjectManager.dataChanged(STATUS.RUNNING, observerProjectManager.getObserverByProject(projectToCompile.getId()), projectToCompile);

        output = projectService.runCode(projectToCompile.getId());
        switch (output.getStatusCode().value()) {
            case 200 ->
                    observerProjectManager.dataChanged(STATUS.RUNNING_SUCCESS, observerProjectManager.getObserverByProject(projectToCompile.getId()), projectToCompile);
            case 400 ->
                    observerProjectManager.dataChanged(STATUS.RUNNING_ERROR, observerProjectManager.getObserverByProject(projectToCompile.getId()), projectToCompile);
        }

        String responseBody = output.getBody();
        JSONObject jsonResponse = new JSONObject(responseBody);
        String outputValue = jsonResponse.getString("output");
        System.out.println("Output: " + outputValue + " -> " + projectToCompile.getId());

        buildCompleted(projectToCompile);

        return output;
    }

    private void buildCompleted(Project newProject) {
        observerProjectManager.dataChanged(STATUS.COMPLETED, observerProjectManager.getObserverByProject(newProject.getId()), newProject);
        observerProjectManager.removeObserver(observerProjectManager.getObserverByProject(newProject.getId()));
        projects.remove(newProject);
        showQueue();
    }

    private Project getNextProjectToCompile() {
        for(Project project: projects) {
            if(observerProjectManager.getObserverByProject(project.getId()).getStatus() == STATUS.WAITING) {
                return project;
            }
        }
        return null;
    }

    private void showQueue() {
        System.out.println();
        for (Project project : projects) {
            if (projects.indexOf(project) == 0) {
                System.out.print("[");
            }
            System.out.print("Index: " + projects.indexOf(project) + " - " + project.getId() + ": " + project.getName());
            if (projects.indexOf(project) != projects.size() - 1) {
                System.out.print(", ");
            } else {
                System.out.println("]");
            }
        }
        System.out.println();
    }

    public Project getProjectById(String projectId) {
        for (Project project : projects) {
            if (project.getId().equals(projectId)) {
                return project;
            }
        }
        return null;
    }

    public List<Project> getProjectsByName(String projectName) {
        List<Project> listProjects = new ArrayList<>();
        for (Project project : projects) {
            if (project.getName().equals(projectName)) {
                listProjects.add(project);
            }
        }
        return listProjects;
    }

    public Project getProjectsByIdAndName(String projectId, String projectName) {
        for (Project project : projects) {
            if (project.getId().equals(projectId) && project.getName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    public boolean deleteProjectById(String projectId) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getId().equals(projectId)) {
                if (observerProjectManager.getObserverByIndex(i).getStatus() == STATUS.WAITING) {
                    projects.remove(projects.get(i));
                    observerProjectManager.removeObserver(observerProjectManager.getObserverByProject(projectId));
                    showQueue();
                    return true;
                }
            }
        }
        return false;
    }

    // Update PATCH
    public boolean partialUpdateProjectById(String projectId, Map<String, Object> updates) {
        Project project = null;

        int posObserver = 0;
        for (Project p : projects) {
            if (p.getId().equals(projectId)) {
                project = p;
                break;
            }
            posObserver++;
        }

        if (project == null || observerProjectManager.getObserverByIndex(posObserver).getStatus() != STATUS.WAITING) {
            return false;
        }

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (field.equals("name") && value instanceof String) {
                project.setName((String) value);
            } else if (field.equals("files") && value instanceof List<?>) {
                List<?> fileList = (List<?>) value;
                if (fileList.stream().allMatch(element -> element instanceof MultipartFile)) {
                    project.setFiles((List<MultipartFile>) value);
                }
            }
        }

        return true;
    }

    // Update PUT
    public boolean updateProjectById(String projectId, Project updatedProject) {
        Project project = null;

        int posObserver = 0;
        for (Project p : projects) {
            if (p.getId().equals(projectId)) {
                project = p;
                break;
            }
            posObserver++;
        }

        if (project == null || observerProjectManager.getObserverByIndex(posObserver).getStatus() != STATUS.WAITING) {
            return false;
        }

        if (updatedProject != null) {
            if (project.getName() != null) {
                project.setName(updatedProject.getName());
            }
            if (project.getFiles() != null) {
                project.setFiles(updatedProject.getFiles());
            }
        }

        return true;
    }

    public ResponseEntity<String> applyFieldMask(List<Project> listProjects, String fields) {
        StringBuilder jsonResponse = new StringBuilder();

        for (Project project : listProjects) {

            if (fields.equals("all")) {
                fields = "id,name,files,status";
            }

            String[] fieldArray = fields.split(",");

            for (String field : fieldArray) {
                switch (field) {
                    case "id":
                        jsonResponse.append("Id: ").append(project.getId()).append("\n");
                        break;
                    case "name":
                        jsonResponse.append("Name: ").append(project.getName()).append("\n");
                        break;
                    case "files": {
                        List<MultipartFile> files = project.getFiles();

                        if (files != null) {
                            jsonResponse.append("Files: [");

                            for (int i = 0; i < files.size(); i++) {
                                MultipartFile file = files.get(i);

                                if (i == 0) {
                                    jsonResponse.append(Paths.get(file.getOriginalFilename()).getFileName().toString());
                                } else {
                                    jsonResponse.append(" ").append(Paths.get(file.getOriginalFilename()).getFileName().toString());
                                }

                                if (i < files.size() - 1) {
                                    jsonResponse.append(",");
                                }
                            }
                            jsonResponse.append("]").append("\n");
                        } else {
                            jsonResponse.append("Files: ").append(project.getFiles()).append("\n");
                        }

                    }
                    break;
                    case "status": {
                        jsonResponse.append("Status: ").append(observerProjectManager.getObserverByProject(project.getId()).getStatus().getMessage()).append("\n");
                    }
                    break;
                }
            }

            jsonResponse.append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(jsonResponse.toString(), headers, HttpStatus.OK);
    }

}

