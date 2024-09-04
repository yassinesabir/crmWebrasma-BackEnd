package com.crud.RankinDigitalCrud.Service;

import com.crud.RankinDigitalCrud.Entity.Lead;
import com.crud.RankinDigitalCrud.Repository.LeadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeadService {
    private final LeadRepository leadRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Lead postLead(Lead lead) {
        // Save lead details in the database
        return leadRepository.save(lead);
    }

    public String saveFile(MultipartFile file) throws IOException {
        // Ensure the directory exists
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Save the file
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = path.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return fileName;
    }

    @Transactional(readOnly = true)
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new EntityNotFoundException("Lead with id " + id + " not found");
        }
        leadRepository.deleteById(id);
    }

    public Lead getLeadById(Long id) {
        return leadRepository.findById(id).orElse(null);
    }

    public Lead updateLead(Long id, Lead lead) {
        Optional<Lead> optionalLead = leadRepository.findById(id);
        if (optionalLead.isPresent()) {
            Lead existingLead = optionalLead.get();
            existingLead.setNom(lead.getNom());
            existingLead.setEmail(lead.getEmail());
            existingLead.setTelephone(lead.getTelephone());
            existingLead.setSource(lead.getSource());
            existingLead.setEntreprise(lead.getEntreprise());
            existingLead.setTag(lead.getTag());
            existingLead.setValeurEstimee(lead.getValeurEstimee());
            existingLead.setDescription(lead.getDescription());
            existingLead.setStatut(lead.getStatut());
            existingLead.setPdfFileName(lead.getPdfFileName()); // Update PDF file name

            return leadRepository.save(existingLead);
        }
        return null;
    }

    public void updateStatusLabel(Long leadId, String newStatusLabel) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new EntityNotFoundException("Lead not found"));
        lead.setStatusLabel(newStatusLabel);
        leadRepository.save(lead);
    }

    public String getCreatorUsername(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new EntityNotFoundException("Lead not found with id " + leadId));
        return lead.getCreatedBy();
    }



    public Path getFilePath(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        if (!Files.exists(filePath)) {
            throw new EntityNotFoundException("File not found");
        }
        return filePath;
    }

}
