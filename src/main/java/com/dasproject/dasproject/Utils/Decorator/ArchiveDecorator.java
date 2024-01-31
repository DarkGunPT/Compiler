package com.dasproject.dasproject.Utils.Decorator;

import com.dasproject.dasproject.Utils.Adapter.CompilerAdapter;
import com.dasproject.dasproject.Utils.Helper;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveDecorator extends Decorator {
    public ArchiveDecorator(CompilerAdapter compilerAdapter) {
        super(compilerAdapter);
    }

    @Override
    public String buildProject(MultipartFile[] files, String projectId) {
        return compilerAdapter.buildProject(files, projectId);
    }

    @Override
    public String runProject(String projectId) {
        return compilerAdapter.runProject(projectId);
    }

    @Override
    public void removeFiles() {
        compilerAdapter.removeFiles();
    }

    @Override
    public void copyFile(InputStream source, String destination) {
        compilerAdapter.copyFile(source, destination);
    }

    @Override
    public String arquivar(String projectId) {
        String folderPath = Helper.createFolderInResources("export");
        if(folderPath.equals("Erro ao criar pasta")){
            return "Erro ao criar pasta export";
        }

        String projectFilesPath = "src/main/resources/files/" + projectId;

        try (FileOutputStream fos = new FileOutputStream(folderPath + "/" + projectId + ".zip");
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            File projectDirectory = new File(projectFilesPath);
            File[] projectFiles = projectDirectory.listFiles();

            if (projectFiles != null) {
                for (File projectFile : projectFiles) {
                    InputStream inputStream = new FileInputStream(projectFile);

                    ZipEntry zipEntry = new ZipEntry(projectFile.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = inputStream.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }

                    inputStream.close();

                    zipOut.closeEntry();
                }
            } else {
                return "No files found in the project directory.";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error during archiving operation: " + e.getMessage();
        }
        return "Files archived successfully!";
    }


}
