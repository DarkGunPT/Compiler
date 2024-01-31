package com.dasproject.dasproject.Utils.Compiler;

import com.dasproject.dasproject.Utils.Adapter.CompilerAdapter;
import com.dasproject.dasproject.Utils.Helper;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JavaCompiler implements CompilerAdapter {
    final String fileUploadDir = "src/main/resources/files";
    private boolean isAllowedFileType(String fileName) {
        return fileName.endsWith(".java");
    }
    private static boolean hasMainMethod(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            boolean mainMethodFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains("public static void main(String[]")) {
                    mainMethodFound = true;
                    break;
                }
            }

            return mainMethodFound;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String buildProject(MultipartFile[] files, String projectId) {
        try {
            String folderName = Helper.createFolderInResources("files");

            if(folderName.equals("Erro ao criar pasta")){
                return "Erro ao criar pasta export";
            }
            String projectFolderPath = Helper.createFolderInResources("files/"+projectId);

            if(projectFolderPath.equals("Erro ao criar pasta")){
                return "Erro ao criar pasta export";
            }

            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!isAllowedFileType(file.getOriginalFilename())) {
                    continue;
                }
                String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
                fileNames.add(fileName);
                Path filePath = Paths.get(projectFolderPath, fileName);

                copyFile(file.getInputStream(), filePath.toString());
            }

            // Compile Java source files
            List<String> javacCommand = new ArrayList<>();
            javacCommand.add("javac");
            String mainClass = null;
            for (String fileName : fileNames) {
                if(mainClass == null){
                    if (hasMainMethod(Path.of(projectFolderPath, fileName))) {
                        mainClass = fileName;
                    }
                }
                javacCommand.add(fileName);
            }

            ProcessBuilder pbCompile = new ProcessBuilder(javacCommand);
            pbCompile.directory(new File(projectFolderPath));
            Process processCompile = pbCompile.start();
            processCompile.waitFor();

            // Create JAR file
            List<String> command = new ArrayList<>();
            command.add("jar");
            command.add("cfe");
            command.add("exec.jar");
            command.add(mainClass.replace(".java",""));
            for (String fileName : fileNames) {
                command.add(fileName);
                command.add(fileName.replace(".java",".class"));
            }

            ProcessBuilder pbCreateJar = new ProcessBuilder(command);
            pbCreateJar.directory(new File(projectFolderPath));
            Process processCreateJar = pbCreateJar.start();
            processCreateJar.waitFor();

            return "BUILDING_SUCCESS";

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "BUILDING_ERROR";
        }
    }

    public String runProject(String projectId) {
        try {
            // Run the compiled program
            ProcessBuilder pb = new ProcessBuilder("java","-jar","exec.jar");
            pb.directory(new File(fileUploadDir + "/" + projectId));
            Process process = pb.start();
            process.getOutputStream().close();

            StringBuilder output = new StringBuilder();
            try (BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean isFirstLine = true;
                while ((line = processReader.readLine()) != null) {
                    output.append(isFirstLine ? "" : "\n").append(line);
                    isFirstLine = false;
                }
            }
            int exitCode = process.waitFor();
            removeFiles();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return output.toString();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Erro interno ao compilar o código.";
        }
    }
    public void removeFiles() {
        File pasta = new File(fileUploadDir);

        if (pasta.isDirectory()) {

            File[] ficheiros = pasta.listFiles();

            if (ficheiros != null) {
                for (File ficheiro : ficheiros) {
                    if (ficheiro.isFile()) {
                        ficheiro.delete();
                    }
                }
            } else {
                System.out.println("Não foi possível listar os ficheiros do diretório.");
            }
        } else {
            System.out.println("O caminho fornecido não é um diretório.");
        }
    }
    public void copyFile(InputStream source, String destination) {
        try (InputStream fis = source;
             OutputStream fos = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


