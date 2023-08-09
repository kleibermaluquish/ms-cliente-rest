package pe.com.encora.app.customers.models.service;

import pe.com.encora.app.customers.models.entity.Customer;

import java.util.List;

public interface ICustomerService {
    Customer save(Customer customer);

    List<Customer> listCustomers();

    Customer findByUniqueCode(String uniqueCode);

    Customer findById(Long id);

}
