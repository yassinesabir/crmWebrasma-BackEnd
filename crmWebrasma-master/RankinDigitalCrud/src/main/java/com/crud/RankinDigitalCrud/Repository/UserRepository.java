package com.crud.RankinDigitalCrud.Repository;


import com.crud.RankinDigitalCrud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
