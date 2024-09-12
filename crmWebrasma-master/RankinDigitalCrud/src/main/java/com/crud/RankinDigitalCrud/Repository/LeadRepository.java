package com.crud.RankinDigitalCrud.Repository;

import com.crud.RankinDigitalCrud.Entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    // Add a method to find leads by a list of IDs
    List<Lead> findByIdIn(List<Long> ids);

    // Existing method
    List<Lead> findByCreatedBy(String createdBy);
}
