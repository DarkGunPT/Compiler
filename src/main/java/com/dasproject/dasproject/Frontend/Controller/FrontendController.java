package com.dasproject.dasproject.Frontend.Controller;

import com.dasproject.dasproject.Backend.Project.Controller.ProjectController;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Controller
@CrossOrigin
public class FrontendController {
    @Autowired
    ProjectController projectController;

    @GetMapping("homepage")
    public String homepage(Model model) {
        model.addAttribute("name", "coming for the controller");
        return "homepage";
    }

    @PostMapping("compile")
    public ResponseEntity<String> compileCode(@RequestParam("directory[]") MultipartFile[] directory){
        return projectController.compileCode(directory);
    }
}