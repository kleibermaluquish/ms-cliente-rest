package pe.com.encora.app.customers.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.encora.app.customers.models.entity.Customer;

public interface ICustomerDao extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where c.codigoUnico = ?1")
    Customer findByUniqueCode(String uniqueCode);
}
