package com.crud.RankinDigitalCrud.Repository;

import com.crud.RankinDigitalCrud.Entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;



@Repository
public interface LeadRepository extends JpaRepository <Lead,Long> {
    List<Lead> findByCreatedBy(String createdBy);
}
