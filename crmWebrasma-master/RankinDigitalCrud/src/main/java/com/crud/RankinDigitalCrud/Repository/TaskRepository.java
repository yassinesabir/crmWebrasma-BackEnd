package com.crud.RankinDigitalCrud.Repository;

import com.crud.RankinDigitalCrud.Entity.Meeting;
import com.crud.RankinDigitalCrud.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
