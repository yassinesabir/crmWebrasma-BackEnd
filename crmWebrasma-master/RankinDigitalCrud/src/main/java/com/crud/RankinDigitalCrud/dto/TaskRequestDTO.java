package com.crud.RankinDigitalCrud.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class TaskRequestDTO {
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String responsible;
    private String description;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
