package com.dasproject.dasproject.Backend.Project.Controller;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import com.dasproject.dasproject.Utils.BuildQueueManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("project")
public class ProjectController {

    @GetMapping("/details")
    public ResponseEntity<String> getProjectDetails(
            @RequestParam(name = "projectId", required = false) String projectId,
            @RequestParam(name = "projectName", required = false) String projectName,
            @RequestParam(name = "fields", defaultValue = "all") String fields) {

        if (projectId != null && projectName != null) {
            return getProjectByIdAndName(projectId, projectName, fields);
        } else if (projectId != null) {
            return getProjectById(projectId, fields);
        } else if (projectName != null) {
            return getProjectByName(projectName, fields);
        } else {
            return getProjects(fields);
        }

    }

    @PostMapping("compile")
    public ResponseEntity<String> compileCode(@RequestParam("directory[]") MultipartFile[] directory) {
        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();
        String name = directory[0].getOriginalFilename().replaceFirst("/.*", "");
        return buildQueueManager.enqueueBuild(directory, name);
    }

    @DeleteMapping("{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable String projectId) {
        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        boolean success = buildQueueManager.deleteProjectById(projectId);

        if (success) {
            System.out.println("\nO projeto com o Id: " + projectId + " foi excluído com sucesso.\n");
            return ResponseEntity.ok("O projeto com o Id: " + projectId + " foi excluído com sucesso.");
        } else {
            System.out.println("\nO projeto com o Id: " + projectId + " não pode ser excluido.\n");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O projeto com o Id: " + projectId + " não pode ser excluido.");
        }
    }

    @PatchMapping("{projectId}")
    public ResponseEntity<String> partialUpdateProject(
            @PathVariable String projectId,
            @RequestBody Map<String, Object> updates) {

        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        boolean success = buildQueueManager.partialUpdateProjectById(projectId, updates);

        if (success) {
            System.out.println("\nO projeto com o Id: " + projectId + " foi atualizado com sucesso.\n");
            return ResponseEntity.ok("O projeto com o Id: " + projectId + " foi atualizado com sucesso.");
        } else {
            System.out.println("\nO projeto com o Id: " + projectId + " não pode ser atualizado.\n");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O projeto com o Id: " + projectId + " não pode ser atualizado.");
        }
    }

    @PutMapping("{projectId}")
    public ResponseEntity<String> updateProject(
            @PathVariable String projectId,
            @RequestBody Project updatedProject) {

        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        boolean success = buildQueueManager.updateProjectById(projectId, updatedProject);

        if (success) {
            System.out.println("\nO projeto com o Id: " + projectId + " foi atualizado com sucesso.\n");
            return ResponseEntity.ok("O projeto com o Id: " + projectId + " foi atualizado com sucesso.");
        } else {
            System.out.println("\nO projeto com o Id: " + projectId + " não pode ser atualizado.\n");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O projeto com o Id: " + projectId + " não pode ser atualizado.");
        }
    }

    public ResponseEntity<String> getProjectById(String projectId, String fields) {
        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        Project project = buildQueueManager.getProjectById(projectId);

        if (project == null) {
            return ResponseEntity.badRequest().body("Nenhum projeto foi encontrado.");
        }

        List<Project> listProjects = new ArrayList<>();
        listProjects.add(project);

        return buildQueueManager.applyFieldMask(listProjects, fields);
    }

    public ResponseEntity<String> getProjectByName(String projectName, String fields) {

        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        List<Project> listProjects = buildQueueManager.getProjectsByName(projectName);

        if (listProjects.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum projeto foi encontrado.");
        }

        return buildQueueManager.applyFieldMask(listProjects, fields);
    }

    public ResponseEntity<String> getProjects(String fields) {

        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        List<Project> listProjects = buildQueueManager.getProjects();

        if (listProjects.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum projeto foi encontrado.");
        }

        return buildQueueManager.applyFieldMask(listProjects, fields);
    }

    public ResponseEntity<String> getProjectByIdAndName(String projectId, String projectName, String fields) {

        BuildQueueManager buildQueueManager = BuildQueueManager.getInstance();

        Project project = buildQueueManager.getProjectsByIdAndName(projectId, projectName);

        if (project == null) {
            return ResponseEntity.badRequest().body("Nenhum projeto foi encontrado.");
        }

        List<Project> listProjects = new ArrayList<>();
        listProjects.add(project);

        return buildQueueManager.applyFieldMask(listProjects, fields);
    }

}
