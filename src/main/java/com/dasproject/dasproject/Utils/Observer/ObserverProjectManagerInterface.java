package com.dasproject.dasproject.Utils.Observer;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import com.dasproject.dasproject.Utils.STATUS;

public interface ObserverProjectManagerInterface {
    void notifyObservers(STATUS STATUS, ObserverProject observer, Project project);
    void addObserver(ObserverProject o);
    void removeObserver(ObserverProject observer);
}
