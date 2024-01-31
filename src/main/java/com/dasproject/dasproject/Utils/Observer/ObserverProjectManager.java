package com.dasproject.dasproject.Utils.Observer;

import com.dasproject.dasproject.Backend.Project.Entity.Project;
import com.dasproject.dasproject.Utils.STATUS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ObserverProjectManager implements ObserverProjectManagerInterface{
    List<ObserverProject> observers = new ArrayList<>();

    public List<ObserverProject> getObservers() {
        return observers;
    }

    @Override
    public void notifyObservers(STATUS STATUS, ObserverProject observer, Project project) {
        observer.update(STATUS, project);
    }

    @Override
    public void addObserver(ObserverProject o) {
        observers.add(o);
    }

    public ObserverProject getObserverByIndex(int index){
        return observers.get(index);
    }
    public ObserverProject getObserverByProject(String projectId){
        for (ObserverProject observer : observers) {
            if(observer.getProjectId().equals(projectId)){
                return observer;
            }
        }
        return null;
    }
    @Override
    public void removeObserver(ObserverProject observer) {
        observers.remove(observer);
    }
    public void dataChanged(STATUS STATUS, ObserverProject observer, Project project){
        notifyObservers(STATUS, observer, project);
    }
}
