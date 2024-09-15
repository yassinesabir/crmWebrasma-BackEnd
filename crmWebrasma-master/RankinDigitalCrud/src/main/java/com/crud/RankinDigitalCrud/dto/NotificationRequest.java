package com.crud.RankinDigitalCrud.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class NotificationRequest {
    @NotEmpty
    private List<Long> leads; // List of IDs
    @NotEmpty
    private String subject;
    @NotEmpty
    private String message;
    @NotEmpty
    private String sentBy;

    // Getters and setters
    public List<Long> getLeads() {
        return leads;
    }

    public void setLeads(List<Long> leads) {
        this.leads = leads;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
