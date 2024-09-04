package com.crud.RankinDigitalCrud.Controller;

import com.crud.RankinDigitalCrud.Entity.Lead;
import com.crud.RankinDigitalCrud.Repository.LeadRepository;
import com.crud.RankinDigitalCrud.Service.ExcelService;
import com.crud.RankinDigitalCrud.Service.LeadService;
import com.crud.RankinDigitalCrud.Service.PdfService;
import com.crud.RankinDigitalCrud.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class LeadController {
    private final LeadService leadService;
    private final LeadRepository leadRepository;
    private final ExcelService excelService;
    private final PdfService pdfService;
    private final JwtUtil jwtUtil;

    @GetMapping("/hello")
    @PreAuthorize("hasRole('COMMERCIAL')")
    public String hello() {
        return "YASSINE ZWIN";
    }

    @GetMapping("/hello2")
    @PreAuthorize("hasRole('MANAGER')")
    public String hello2() {
        return "Hasnaa lkhyba";
    }

    @PostMapping("/Lead")
    public ResponseEntity<?> postLeadWithFile(
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("source") String source,
            @RequestParam("entreprise") String entreprise,
            @RequestParam("tag") String tag,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {

        // Extract the user's full name from the JWT token
        String fullName = jwtUtil.extractFullNameFromRequest(request);

        Lead lead = new Lead();
        lead.setNom(nom);
        lead.setEmail(email);
        lead.setTelephone(String.valueOf(Long.parseLong(telephone)));
        lead.setSource(source);
        lead.setEntreprise(entreprise);
        lead.setTag(tag);
        lead.setDescription(description);
        lead.setCreatedBy(fullName); // Set the human-readable name

        if (file != null && !file.isEmpty()) {
            try {
                String fileName = leadService.saveFile(file);
                lead.setPdfFileName(fileName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to store file: " + e.getMessage());
            }
        }

        Lead savedLead = leadService.postLead(lead);
        return ResponseEntity.ok(savedLead);
    }

    @GetMapping("/Leads")
    public ResponseEntity<List<Lead>> getAllLeads() {
        List<Lead> leads = leadService.getAllLeads();
        return ResponseEntity.ok(leads);
    }

    @DeleteMapping("/Lead/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        try {
            leadService.deleteLead(id);
            return new ResponseEntity<>("Lead with id " + id + " deleted successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/Lead/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id) {
        Lead lead = leadService.getLeadById(id);
        if (lead == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(lead);
    }

    @PatchMapping("/Lead/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody Lead lead) {
        Lead updatedLead = leadService.updateLead(id, lead);
        if (updatedLead == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.ok(updatedLead);
    }

    @PostMapping("/Lead/{id}/update-file")
    public ResponseEntity<?> updateLeadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Lead lead = leadService.getLeadById(id);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String fileName = leadService.saveFile(file);
            lead.setPdfFileName(fileName);
            leadService.postLead(lead);
            return ResponseEntity.ok("File updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update file: " + e.getMessage());
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importLeads(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // Extract the user's full name from the JWT token
            String createdBy = jwtUtil.extractFullNameFromRequest(request);

            excelService.importLeadsFromExcel(file, createdBy);
            return ResponseEntity.ok("Leads imported successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import leads: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Lead>> getLeadsByLoggedInUser(HttpServletRequest request) {
        String fullName = jwtUtil.extractFullNameFromRequest(request);
        List<Lead> leads = leadRepository.findByCreatedBy(fullName);
        return ResponseEntity.ok(leads);
    }

    @PutMapping("/leads/{id}/status-label")
    public ResponseEntity<String> updateStatusLabel(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newStatusLabel = request.get("statusLabel");
        leadService.updateStatusLabel(id, newStatusLabel);
        return ResponseEntity.ok("Status label updated successfully");
    }

    @GetMapping("/Lead/{id}/download-file")
    public ResponseEntity<Resource> downloadLeadFile(@PathVariable Long id) {
        Lead lead = leadService.getLeadById(id);
        if (lead == null || lead.getPdfFileName() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Path filePath = leadService.getFilePath(lead.getPdfFileName());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lead.getPdfFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/Lead/{id}/export-pdf")
    public ResponseEntity<byte[]> exportLeadToPdf(@PathVariable Long id) {
        try {
            Lead lead = leadService.getLeadById(id);
            if (lead == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] pdfBytes = pdfService.generateLeadPdf(lead);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lead-" + id + ".pdf\"")
                    .body(pdfBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
