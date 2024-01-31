package com.dasproject.dasproject.Backend.Project.ProjectService;

import com.dasproject.dasproject.Utils.Adapter.CompilerAdapter;
import com.dasproject.dasproject.Utils.Compiler.Compiler;
import com.dasproject.dasproject.Utils.Compiler.JavaCompiler;
import com.dasproject.dasproject.Utils.Decorator.ArchiveDecorator;
import com.dasproject.dasproject.Utils.Decorator.Decorator;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProjectService {
    CompilerAdapter compilerAdapter = null;

    public ResponseEntity<String> buildCode(MultipartFile[] directory, String projectId) {
        try {
            if(directory == null)
                return ResponseEntity.badRequest().body("Diretoria não fornecida.");
            AtomicReference<String> type = new AtomicReference<>("");
            List<MultipartFile> auxDir = Arrays.stream(directory).toList();
            auxDir.forEach(multipartFile -> {
                if(multipartFile.getOriginalFilename().endsWith(".java")) {
                    type.set("java");

                }else if(multipartFile.getOriginalFilename().endsWith(".c")) {
                    type.set("c");

                }if(multipartFile.getOriginalFilename().endsWith(".cpp")) {
                    type.set("c++");
                }
            });

            switch (type.get()){
                case "c" -> compilerAdapter = new Compiler();
                case "c++"-> compilerAdapter = new Compiler();
                case "java"-> compilerAdapter = new JavaCompiler();
            }

            String result = compilerAdapter.buildProject(directory, projectId);
            Decorator archiveDecorator = new ArchiveDecorator(compilerAdapter);
            archiveDecorator.arquivar(projectId);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("output", result);

            return !result.contains("Erro interno ao compilar o código.") ?
                    ResponseEntity.ok(jsonResponse.toString()) :
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao compilar o código.");
        }
    }
    public ResponseEntity<String> runCode(String projectId) {
        try {

            String result = compilerAdapter.runProject(projectId);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("output", result);

            return !result.contains("Erro interno ao compilar o código.") ?
                    ResponseEntity.ok(jsonResponse.toString()) :
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao compilar o código.");
        }
    }
}
