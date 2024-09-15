package com.crud.RankinDigitalCrud.Controller;

import com.crud.RankinDigitalCrud.Entity.Lead;
import com.crud.RankinDigitalCrud.Repository.LeadRepository;
import com.crud.RankinDigitalCrud.Service.EmailService;
import com.crud.RankinDigitalCrud.Service.LeadService;
import com.crud.RankinDigitalCrud.dto.NotificationRequest;
import com.crud.RankinDigitalCrud.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private LeadService leadService;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil

    @GetMapping("/leads")
    public ResponseEntity<List<Lead>> getLeads() {
        List<Lead> leads = leadService.getAllLeads(); // Adjust as necessary
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Lead>> getLeadsByLoggedInUser(HttpServletRequest request) {
        String fullName = jwtUtil.extractFullNameFromRequest(request);
        List<Lead> leads = leadRepository.findByCreatedBy(fullName);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/email-subjects")
    public ResponseEntity<List<String>> getEmailSubjects() {
        List<String> subjects = Arrays.asList(
                "Action Requise",
                "Informations Importantes",
                "Mise à Jour",
                "Réponse Demandée",
                "Suivi Urgent"
        );
        return ResponseEntity.ok(subjects);
    }

    @PostMapping("/send-notification")
    public ResponseEntity<Void> sendNotification(
            @RequestBody NotificationRequest request,
            HttpServletRequest httpRequest) { // Add HttpServletRequest parameter
        // Extract the user's full name from the JWT token
        String sentBy = jwtUtil.extractFullNameFromRequest(httpRequest);

        emailService.sendNotification(
                request.getLeads(),
                request.getSubject(),
                request.getMessage(),
                sentBy // Use extracted name
        );
        return ResponseEntity.ok().build();
    }
}
