package com.crud.RankinDigitalCrud.Controller;

import com.crud.RankinDigitalCrud.Entity.Meeting;
import com.crud.RankinDigitalCrud.Entity.Task;
import com.crud.RankinDigitalCrud.Service.MeetingService;
import com.crud.RankinDigitalCrud.Service.TaskService;
import com.crud.RankinDigitalCrud.dto.MeetingRequestDTO;
import com.crud.RankinDigitalCrud.dto.TaskRequestDTO;
import com.crud.RankinDigitalCrud.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MeetingController {

    private final MeetingService meetingService;
    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create-meeting")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequestDTO meetingRequestDTO, HttpServletRequest request) {
        // Extract the user's full name from the JWT token
        String plannedBy = jwtUtil.extractFullNameFromRequest(request);

        // Create a new Meeting object
        Meeting meeting = new Meeting();
        meeting.setTitle(meetingRequestDTO.getTitle());
        meeting.setDateTime(LocalDateTime.of(meetingRequestDTO.getDate(), meetingRequestDTO.getTime()));
        meeting.setLeadEmail(meetingRequestDTO.getLeadEmail());
        meeting.setPlannedBy(plannedBy); // Set the name of the user who planned the meeting

        Meeting createdMeeting = meetingService.createMeeting(meeting);

        return ResponseEntity.ok(createdMeeting);
    }

    @GetMapping("/meetings/{id}")
    public ResponseEntity<?> getMeetingById(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingService.getMeetingById(id);
        return meeting.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/meetings")
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/export-calendar")
    public void exportMeetings(HttpServletResponse response) throws IOException {
        List<Meeting> meetings = meetingService.getAllMeetings();

        // Start constructing the iCal file content
        StringBuilder icalContent = new StringBuilder();
        icalContent.append("BEGIN:VCALENDAR\n");
        icalContent.append("VERSION:2.0\n");
        icalContent.append("PRODID:-//Your Company//NONSGML v1.0//EN\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

        for (Meeting meeting : meetings) {
            LocalDateTime startDateTime = meeting.getDateTime();
            LocalDateTime endDateTime = startDateTime.plusHours(1); // Assuming 1 hour duration

            String startDate = startDateTime.format(formatter);
            String endDate = endDateTime.format(formatter);

            icalContent.append("BEGIN:VEVENT\n");
            icalContent.append("SUMMARY:").append(meeting.getTitle()).append("\n");
            icalContent.append("DTSTART:").append(startDate).append("\n");
            icalContent.append("DTEND:").append(endDate).append("\n");
            icalContent.append("UID:").append(meeting.getId()).append("\n");
            icalContent.append("END:VEVENT\n");
        }

        icalContent.append("END:VCALENDAR\n");

        // Set the response content type and header
        response.setContentType("text/calendar");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=meetings.ics");
        response.getOutputStream().write(icalContent.toString().getBytes());
        response.getOutputStream().flush();
    }

    @PostMapping("/import-calendar")
    public ResponseEntity<?> importMeetings(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String plannedBy = jwtUtil.extractFullNameFromRequest(request);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            String summary = null;
            String startDateTimeStr = null;
            String endDateTimeStr = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("SUMMARY:")) {
                    summary = line.substring("SUMMARY:".length()).trim();
                } else if (line.startsWith("DTSTART:")) {
                    startDateTimeStr = line.substring("DTSTART:".length()).trim();
                    // Handle timezones
                    if (startDateTimeStr.contains("TZID")) {
                        startDateTimeStr = startDateTimeStr.replaceAll("TZID=.*:", ""); // Simplify to ISO format
                    }
                } else if (line.startsWith("DTEND:")) {
                    endDateTimeStr = line.substring("DTEND:".length()).trim();
                    // Handle timezones
                    if (endDateTimeStr.contains("TZID")) {
                        endDateTimeStr = endDateTimeStr.replaceAll("TZID=.*:", ""); // Simplify to ISO format
                    }
                }

                if (summary != null && startDateTimeStr != null && endDateTimeStr != null) {
                    try {
                        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, formatter);
                        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr, formatter);

                        Meeting meeting = new Meeting();
                        meeting.setTitle(summary);
                        meeting.setDateTime(startDateTime);
                        meeting.setPlannedBy(plannedBy);

                        meetingService.createMeeting(meeting);

                        summary = null;
                        startDateTimeStr = null;
                        endDateTimeStr = null;
                    } catch (Exception e) {
                        // Log and continue on parsing errors
                        System.err.println("Error parsing meeting data: " + e.getMessage());
                    }
                }
            }

            return ResponseEntity.ok("Meetings imported successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to import meetings: " + e.getMessage());
        }
    }


    @PostMapping("/add-task")
    public ResponseEntity<?> addTask(@RequestBody TaskRequestDTO taskRequestDTO, HttpServletRequest request) {
        String responsible = jwtUtil.extractFullNameFromRequest(request);
        // Create a new Task object
        Task task = new Task();
        task.setTitle(taskRequestDTO.getTitle());
        task.setDateTime(LocalDateTime.of(taskRequestDTO.getDate(), taskRequestDTO.getTime()));
        task.setResponsible(responsible);
        task.setDescription(taskRequestDTO.getDescription());

        Task createdTask = taskService.createTask(task);

        return ResponseEntity.ok(createdTask);
    }

    @DeleteMapping("/meetings/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable Long id) {
        boolean isDeleted = meetingService.deleteMeeting(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        boolean isDeleted = taskService.deleteTask(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
    }

}
