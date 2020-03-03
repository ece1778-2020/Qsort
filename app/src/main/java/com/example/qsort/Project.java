package com.example.qsort;

import androidx.annotation.NonNull;

public class Project {

    String projectName, projectId,pictureUri, designerUid, timestamp;
    int noParticipants;
    String categories, labels;

    public Project(String projectName, String projectId, String designerUid, String timestamp, int noParticipants,String pictureUri, String categories, String labels){
        this.projectName = projectName;
        this.projectId = projectId;
        this.designerUid = designerUid;
        this.timestamp = timestamp;
        this.noParticipants = noParticipants;
        this.pictureUri = pictureUri;
        this.categories = categories;
        this.labels = categories;

    }

    public Project(String projectName, String pictureUri, String projectId){
        this.projectName = projectName;
        this.pictureUri = pictureUri;
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public int getNoParticipants() {
        return noParticipants;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public String getCategories() {
        return categories;
    }

    public String getLabels() {
        return labels;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDesignerUid() {
        return designerUid;
    }
}

