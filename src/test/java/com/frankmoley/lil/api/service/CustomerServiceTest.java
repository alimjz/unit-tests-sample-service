package com.frankmoley.lil.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.frankmoley.lil.api.data.entity.CustomerEntity;
import com.frankmoley.lil.api.data.repository.CustomerRepository;
import com.frankmoley.lil.api.web.error.ConflictException;
import com.frankmoley.lil.api.web.error.NotFoundException;
import com.frankmoley.lil.api.web.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @InjectMocks
  private CustomerService service;

  @Mock
  CustomerRepository customerRepository;



  @Test
  void getAllCustomers() {
    Mockito.doReturn(getMockCustomers(2)).when(customerRepository).findAll();
    List<Customer> customers = service.getAllCustomers();
    assertEquals(2, StreamSupport.stream(customers.spliterator(), false).count());
  }

  @Test
  void getCustomer() {
    CustomerEntity entity = getMockCustomerEntity();
    Optional<CustomerEntity> optional = Optional.of(entity);
    Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());

    Customer customer = service.getCustomer(entity.getCustomerId().toString());
    assertNotNull(customer);
    assertEquals("testFirst", customer.getFirstName());
  }

  @Test
  void getCustomer_notExists() {
    CustomerEntity entity = getMockCustomerEntity();
    Optional<CustomerEntity> optional = Optional.empty();
    Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());
    assertThrows(NotFoundException.class, ()->service.getCustomer(entity.getCustomerId().toString()), "exception not throw as expected");

  }


  @Test
  void findByEmailAddress() {
    CustomerEntity entity = getMockCustomerEntity();

    Mockito.doReturn(entity).when(customerRepository).findByEmailAddress(entity.getEmailAddress());
    Customer customer = service.findByEmailAddress(entity.getEmailAddress());
    assertNotNull(customer);
    assertEquals("testFirst", customer.getFirstName());
  }

  @Test
  void addCustomer() {
    CustomerEntity entity = getMockCustomerEntity();
    when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(null);
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entity);
    Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(),
        entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
    customer = service.addCustomer(customer);
    assertNotNull(customer);
    assertEquals("testLast", customer.getLastName());
  }

  @Test
  void addCustomer_existing() {
    CustomerEntity entity = getMockCustomerEntity();
    when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(entity);
    Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(),
        entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
    assertThrows(ConflictException.class, () -> service.addCustomer(customer), "should have thrown conflict exception");

  }

  @Test
  void updateCustomer() {
    CustomerEntity entity = getMockCustomerEntity();
    when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entity);
    Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(),
        entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
    customer = service.updateCustomer(customer);
    assertNotNull(customer);
    assertEquals("testLast", customer.getLastName());
  }

  @Test
  void deleteCustomer() {
    UUID id = UUID.randomUUID();
    doNothing().when(customerRepository).deleteById(id);
    service.deleteCustomer(id.toString());
  }


  private CustomerEntity getMockCustomerEntity(){
    return new CustomerEntity(UUID.randomUUID(), "testFirst", "testLast", "testemail@test.com", "555-515-1234", "1234 Test Street");
  }

  private List<CustomerEntity> getMockCustomers(int count){
    List<CustomerEntity> customers = new ArrayList<>(count);
    for(int i=0;i<count;i++){
      customers.add(new CustomerEntity(UUID.randomUUID(), "firstName" + i, "lastName" + i, "email" + i + "@test.com", "555-123-1212", "1234 Main Street #" + i));
    }
    return customers;
  }
}

