package com.dasproject.dasproject.Utils.Observer;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import com.dasproject.dasproject.Utils.STATUS;

public interface ObserverProjectInterface {
    void update(STATUS STATUS, Project project);
}
