package com.biz4solutions.models.request;

public class IncidentReport {
    private String comment;
    private boolean isVictimLifeSaved;
    private String link;
    private String name;
    private String requestId;
    private String title;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isVictimLifeSaved() {
        return isVictimLifeSaved;
    }

    public void setVictimLifeSaved(boolean victimLifeSaved) {
        isVictimLifeSaved = victimLifeSaved;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "IncidentReport{" +
                "comment='" + comment + '\'' +
                ", isVictimLifeSaved=" + isVictimLifeSaved +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", requestId='" + requestId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
