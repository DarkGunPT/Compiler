package com.dasproject.dasproject.Utils.Decorator;

import com.dasproject.dasproject.Utils.Adapter.CompilerAdapter;
import org.springframework.web.multipart.MultipartFile;

public abstract class Decorator implements CompilerAdapter {
    CompilerAdapter compilerAdapter;
    public Decorator(CompilerAdapter compilerAdapter){
        this.compilerAdapter = compilerAdapter;
    }
    abstract public String arquivar(String projectId);
}
