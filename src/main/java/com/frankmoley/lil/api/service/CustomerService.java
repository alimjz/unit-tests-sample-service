package com.frankmoley.lil.api.service;

import com.frankmoley.lil.api.data.entity.CustomerEntity;
import com.frankmoley.lil.api.data.repository.CustomerRepository;
import com.frankmoley.lil.api.web.controller.ControllerUtils;
import com.frankmoley.lil.api.web.error.ConflictException;
import com.frankmoley.lil.api.web.error.NotFoundException;
import com.frankmoley.lil.api.web.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public List<Customer> getAllCustomers(){
    Iterable<CustomerEntity> entities = this.customerRepository.findAll();
    List<Customer> customers = new ArrayList<>();
    entities.forEach(entity -> {
      customers.add(translateDbToWeb(entity));
    });
    return customers;
  }

  public Customer findByEmailAddress(String emailAddress){
    CustomerEntity entity = this.customerRepository.findByEmailAddress(emailAddress);
    return this.translateDbToWeb(entity);
  }

  public Customer addCustomer(Customer customer){
    Customer existing = this.findByEmailAddress(customer.getEmailAddress());
    if(existing != null){
      throw new ConflictException("customer with email already exists");
    }
    CustomerEntity entity = this.translateWebToDb(customer, true);
    entity = this.customerRepository.save(entity);
    return this.translateDbToWeb(entity);
  }

  public Customer getCustomer(String id) {
    UUID customerId = ControllerUtils.translateStringToUUID(id);
    Optional<CustomerEntity> optionalEntity = this.customerRepository.findById(customerId);
    if(optionalEntity.isEmpty()){
      throw new NotFoundException("customer not found with id");
    }
    return this.translateDbToWeb(optionalEntity.get());
  }

  public Customer updateCustomer(Customer customer){
    CustomerEntity entity = this.translateWebToDb(customer, false);
    entity = this.customerRepository.save(entity);
    return this.translateDbToWeb(entity);
  }

  public void deleteCustomer(String id){
    UUID customerId = ControllerUtils.translateStringToUUID(id);
    this.customerRepository.deleteById(customerId);
  }

  private Customer translateDbToWeb(CustomerEntity entity) {
    if (entity == null){
      return null;
    }
    return new Customer(entity.getCustomerId().toString(), entity.getFirstName(),
        entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
  }

  private CustomerEntity translateWebToDb(Customer customer, boolean createId){
    UUID id;
    if(createId){
      id = UUID.randomUUID();
    }else{
      id = ControllerUtils.translateStringToUUID(customer.getCustomerId());
    }
    return new CustomerEntity(id, customer.getFirstName(), customer.getLastName(),
        customer.getEmailAddress(), customer.getPhoneNumber(), customer.getAddress());
  }



}
