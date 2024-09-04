package com.crud.RankinDigitalCrud.Service;

import com.crud.RankinDigitalCrud.Entity.Lead;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final LeadService leadService;

    public void importLeadsFromExcel(MultipartFile file, String createdBy) throws IOException {
        List<Lead> leads = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // skip the header row
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Lead lead = new Lead();

                // Assuming the order of columns is:
                // 1. Nom, 2. Email, 3. Telephone, 4. Source, 5. Entreprise, 6. Tag, 7. Description, 8. Statut

                // Column 0: Nom
                lead.setNom(getStringCellValue(currentRow.getCell(0)));

                // Column 1: Email
                lead.setEmail(getStringCellValue(currentRow.getCell(1)));

                // Column 2: Telephone
                String telephoneString = getStringCellValue(currentRow.getCell(2)).replaceAll("[^0-9]", "");
                if (!telephoneString.isEmpty()) {
                    lead.setTelephone(String.valueOf(Long.parseLong(telephoneString)));
                }

                // Column 3: Source
                lead.setSource(getStringCellValue(currentRow.getCell(3)));

                // Column 4: Entreprise
                lead.setEntreprise(getStringCellValue(currentRow.getCell(4)));

                // Column 5: Tag
                lead.setTag(getStringCellValue(currentRow.getCell(5)));

                // Column 6: Tag
                lead.setTag(getStringCellValue(currentRow.getCell(6)));


                // Column 7: Description
                lead.setDescription(getStringCellValue(currentRow.getCell(7)));

                // Column 8: Statut (optional, default to "Nouveau")
                String statut = getStringCellValue(currentRow.getCell(8));
                if (statut != null && !statut.isEmpty()) {
                    lead.setStatut(statut);
                } else {
                    lead.setStatut("Nouveau"); // Default value
                }

                lead.setDateCreation(LocalDateTime.now());
                lead.setCreatedBy(createdBy);


                leads.add(lead);
            }
        }

        // Save all leads in the database
        for (Lead lead : leads) {
            // No file associated with leads from Excel import
            leadService.postLead(lead);
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue().trim();
        }
        return null;
    }
}
