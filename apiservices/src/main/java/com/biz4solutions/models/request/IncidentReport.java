package com.biz4solutions.models.request;

public class IncidentReport {
    private String comment;
    private boolean isVictimLifeSaved;
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
                ", requestId='" + requestId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
