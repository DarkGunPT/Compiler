package com.dasproject.dasproject.Utils;

import java.io.*;

public class Helper {
    public static String  createFolderInResources(String folderName){
        String folderPath = "src/main/resources/"+folderName+"/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                return "Erro ao criar pasta";
            }
        }
        return folderPath;
    }
}