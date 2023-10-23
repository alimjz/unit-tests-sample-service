package com.frankmoley.lil.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.frankmoley.lil.api.web.error.ConflictException;
import com.frankmoley.lil.api.web.error.NotFoundException;
import com.frankmoley.lil.api.web.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class CustomerServiceIntegrationTest {

  @Autowired
  private CustomerService service;

  @Test
  void getAllCustomers(){
    List<Customer> customers = this.service.getAllCustomers();
    assertEquals(5, customers.size());
  }

  @Test
  void getCustomer(){
    Customer customer = this.service.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");
    assertNotNull(customer);
    assertEquals("Cally", customer.getFirstName());
  }

  @Test
  void getCustomer_NotFound(){
    assertThrows(NotFoundException.class, () -> this.service.getCustomer("d972b30f-21cc-411f-b374-685ce23cd317"), "should have thrown an exception");
  }

  @Test
  void addCustomer(){
    Customer customer = new Customer("", "John", "Doe", "jdoe@test.com", "555-515-1234", "1234 Main Street; Anytown, KS 66110");
    customer = this.service.addCustomer(customer);
    assertTrue(StringUtils.isNotBlank(customer.getCustomerId()));
    assertEquals("John", customer.getFirstName());
    //cleaning up
    this.service.deleteCustomer(customer.getCustomerId());
  }

  @Test
  void addCustomer_alreadyExists(){
    Customer customer = new Customer("", "John", "Doe", "penatibus.et@lectusa.com", "555-515-1234", "1234 Main Street; Anytown, KS 66110");
    assertThrows(ConflictException.class, () -> this.service.addCustomer(customer));
  }

  @Test
  void updateCustomer(){
    Customer customer = new Customer("", "John", "Doe", "jdoe@test.com", "555-515-1234", "1234 Main Street; Anytown, KS 66110");
    customer = this.service.addCustomer(customer);
    customer.setFirstName("Jane");
    customer = this.service.updateCustomer(customer);
    assertEquals("Jane", customer.getFirstName());
    //cleaning up
    this.service.deleteCustomer(customer.getCustomerId());
  }

}


