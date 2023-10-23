package com.frankmoley.lil.api.data.repository;

import com.frankmoley.lil.api.data.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<CustomerEntity, UUID> {
  CustomerEntity findByEmailAddress(String emailAddress);
}
