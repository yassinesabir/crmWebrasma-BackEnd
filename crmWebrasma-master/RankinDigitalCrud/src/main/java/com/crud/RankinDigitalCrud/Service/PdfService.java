package com.crud.RankinDigitalCrud.Service;

import com.crud.RankinDigitalCrud.Entity.Lead;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PdfService {
    public byte[] generateLeadPdf(Lead lead) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);

            // Set page size if necessary (A4 size as an example)
            pdf.setDefaultPageSize(PageSize.A4);

            Document document = new Document(pdf);
            document.setBottomMargin(50); // Ensure there is space at the bottom for the footer

            // Font setup
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bodyFontSemiBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD); // Use semi-bold for values

            // Add company information at the top-left
            Paragraph companyInfo = new Paragraph("Rankin Digital,\n353, Boulevard Mohamed V, \nEspace Idriss, 10 ème étage,\nCasablanca,\nPhone: +212 620-096270\nEmail: contact@rankindigital.ma\n")
                    .setFont(headerFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(20) // Adjust top margin as needed
                    .setMarginLeft(20); // Adjust left margin as needed

            document.add(companyInfo);

            // Add logo at the top-right
            Image logo = new Image(ImageDataFactory.create("C:\\Users\\fj\\Downloads\\crmWebrasma-master\\crmWebrasma-master\\RankinDigitalCrud\\src\\main\\resources\\static\\images\\img.png")); // Update with the path to your logo
            logo.setWidth(100); // Set width for the logo

            // Position logo at the top-right
            logo.setFixedPosition(pdf.getDefaultPageSize().getWidth() - 120, pdf.getDefaultPageSize().getHeight() - 135, 100);

            document.add(logo);

            // Add lead details section
            document.add(new Paragraph("Détail du lead :")
                    .setFont(headerFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(100) // Adjust top margin to separate from the logo
                    .setMarginBottom(50));

            float[] columnWidths = {2, 4}; // Adjust column widths as needed
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100% of page width

            // Header row
            DeviceRgb headerColor = new DeviceRgb(135, 94, 248); // Color #875EF8
            Cell headerCell1 = new Cell().add(new Paragraph("Parameter").setFont(headerFont)).setBackgroundColor(headerColor).setTextAlignment(TextAlignment.CENTER);
            Cell headerCell2 = new Cell().add(new Paragraph("Value").setFont(headerFont)).setBackgroundColor(headerColor).setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(headerCell1);
            table.addHeaderCell(headerCell2);

            // Data rows
            String[] headers = {"ID", "Name", "Email", "Telephone", "Source", "Entreprise", "Tag", "Valeur Estimée", "Statut", "Créer Par", "Date Création"};
            String[] values = {
                    lead.getId().toString(),
                    lead.getNom(),
                    lead.getEmail(),
                    String.valueOf(lead.getTelephone()),
                    lead.getSource(),
                    lead.getEntreprise(),
                    lead.getTag(),
                    String.valueOf(lead.getValeurEstimee()),
                    lead.getStatut(),
                    lead.getCreatedBy(),
                    lead.getDateCreation().toString(),
            };

            for (int i = 0; i < headers.length; i++) {
                table.addCell(new Paragraph(headers[i]).setFont(headerFont).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Paragraph(values[i]).setFont(bodyFontSemiBold).setTextAlignment(TextAlignment.CENTER)); // Semi-bold for values
            }

            document.add(table);

            // Add footer
            int currentYear = LocalDate.now().getYear(); // Get the current year dynamically
            document.add(new Paragraph("Copyright © " + currentYear + " - Rankin digital Agency.")
                    .setFont(bodyFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFixedPosition(0, 50, pdf.getDefaultPageSize().getWidth())); // Position footer at the bottom of the page

            document.close();

            return baos.toByteArray();  // Return as byte array
        }
    }
}
