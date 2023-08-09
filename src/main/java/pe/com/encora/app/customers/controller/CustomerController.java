package pe.com.encora.app.customers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.com.encora.app.customers.models.entity.Customer;
import pe.com.encora.app.customers.models.service.ICustomerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/clientes")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearCliente(@Validated @RequestBody Customer cliente) {

        Customer clienteNew;
        Map<String, Object> response = new HashMap<>();

        try {
            clienteNew = customerService.save(cliente);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar el cliente en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("respuesta", "El cliente ha sido creado con éxito!");
        response.put("cliente", clienteNew);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarClientes() {
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @GetMapping("/ver/codigoUnico/{uniqueCode}")
    public ResponseEntity<?> verClientePorCodigoUnico(@PathVariable String uniqueCode) {

        Customer customer;
        Map<String, Object> response = new HashMap<>();

        try {
            customer = customerService.findByUniqueCode(uniqueCode);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.isNull(customer)) {
            response.put("mensaje", "El cliente ID: ".concat(uniqueCode.toString()).concat(", no existe en la base de datos."));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @GetMapping("/ver/id/{id}")
    public ResponseEntity<?> verClientePorId(@PathVariable Long id) {

        Customer cliente;
        Map<String, Object> response = new HashMap<>();

        try {
            cliente = customerService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.isNull(cliente)) {
            response.put("mensaje", "El cliente ID: ".concat(id.toString()).concat(", no existe en la base de datos."));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PostMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarCliente(@Validated @RequestBody Customer customer, @PathVariable Long id) {

        Customer cliente = customerService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (Objects.isNull(cliente)) {
            response.put("mensaje", "Error: no se pudo editar, el cliente ID: ".concat(id.toString())
                    .concat(", no existe en la base de datos."));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            cliente.setNombres(customer.getNombres());
            cliente.setApellidos(customer.getApellidos());
            cliente.setTipoDocumento(customer.getTipoDocumento());
            cliente.setNumeroDocumento(customer.getNumeroDocumento());
            cliente.setCodigoUnico(customer.getCodigoUnico());
            customerService.save(cliente);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el cliente en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("respuesta", "El cliente ha sido actualizado con éxito!");
        response.put("cliente", cliente);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
