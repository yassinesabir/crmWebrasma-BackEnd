package com.crud.RankinDigitalCrud.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class MeetingRequestDTO {
    private String type; // "meeting" or "task"
    private String title;
    private LocalDate date;
    private LocalTime time; // Optional, used only for meetings
    private String leadEmail; // Optional, used only for meetings
    private String plannedBy; // Optional, used only for meetings


    // Getters and setters
    public String getType() { return type; }
    public void setType(String title) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getLeadEmail() { return leadEmail; }
    public void setLeadEmail(String leadEmail) { this.leadEmail = leadEmail; }
    public String getPlannedBy() { return plannedBy; }
    public void setPlannedBy(String plannedBy) { this.plannedBy = plannedBy; }
}
