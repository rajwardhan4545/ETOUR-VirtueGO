package com.example.repositories;

import com.example.entities.CustomerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerMaster, Integer> {
    Optional<CustomerMaster> findByEmail(String email); //Find by Email

    // Assuming we might want to find by name too?
    //Optional<CustomerMaster> findByFirstLastName(String firstName,String LastName);
}
