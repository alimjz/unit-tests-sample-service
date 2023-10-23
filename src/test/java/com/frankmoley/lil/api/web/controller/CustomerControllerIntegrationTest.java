package com.frankmoley.lil.api.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankmoley.lil.api.web.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class CustomerControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void getAllCustomers() throws Exception {
    this.mockMvc.perform(get("/customers")).andExpect(status().isOk())
        .andExpect(content().string(containsString("054b145c-ddbc-4136-a2bd-7bf45ed1bef7")))
        .andExpect(content().string(containsString("38124691-9643-4f10-90a0-d980bca0b27d")));
  }

  @Test
  void getCustomerById() throws Exception {
    this.mockMvc.perform(get("/customers/054b145c-ddbc-4136-a2bd-7bf45ed1bef7")).andExpect(status().isOk())
            .andExpect(content().string(containsString("054b145c-ddbc-4136-a2bd-7bf45ed1bef7")));
  }

  @Test
  void customerNotFound() throws Exception {
    this.mockMvc.perform(get("/customers/054b145c-ddbc-4136-a2bd-7bf45ed1bef9")).andExpect(status().isNotFound());
  }

  @Test
  void updateCustomer() throws Exception {
    Customer customer = new Customer("c04ca077-8c40-4437-b77a-41f510f3f185", "Jack", "Bower", "quam.quis.diam@facilisisfacilisis.org", "(831) 996-1240", "2 Rockefeller Avenue, Waco, TX 76796");
    ObjectMapper objectMapper = new ObjectMapper();
    this.mockMvc.perform(put("/customers/c04ca077-8c40-4437-b77a-41f510f3f185").content(objectMapper.writeValueAsString(customer))
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void createCustomer() throws Exception {
    Customer customer = new Customer(UUID.randomUUID().toString(), "Ali", "MJZ", "fuck@fucker.com", "+989128598759", "2 Rockefeller Avenue, Waco, TX 76796");
    ObjectMapper objectMapper = new ObjectMapper();
    this.mockMvc.perform(post("/customers").content(objectMapper.writeValueAsString(customer))
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
  }

  @Test
  void customerAlreadyExist() throws Exception {
    Customer customer = new Customer("","Cally","Reynolds","penatibus.et@lectusa.com","(901) 166-8355","556 Lakewood Park, Bismarck, ND 58505");
    ObjectMapper objectMapper = new ObjectMapper();
    this.mockMvc.perform(post("/customers").content(objectMapper.writeValueAsString(customer))
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
  }





}
