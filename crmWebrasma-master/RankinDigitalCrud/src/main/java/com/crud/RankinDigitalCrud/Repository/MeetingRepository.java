package com.crud.RankinDigitalCrud.Repository;

import com.crud.RankinDigitalCrud.Entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}

