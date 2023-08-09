package pe.com.encora.app.customers.models.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.encora.app.customers.models.dao.ICustomerDao;
import pe.com.encora.app.customers.models.entity.Customer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements ICustomerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private ICustomerDao customerDao;

    @Override
    public Customer save(Customer customer) {
        LOGGER.info("[PASO 1] - encriptar cadena");
        String cadenaEncriptada = encriptarCadena(customer, customer.getCodigoUnico());

        LOGGER.info("[PASO 2] - asignar cadena encriptada(unique code) a customer");
        customer.setCodigoUnico(cadenaEncriptada);

        LOGGER.info("[******** ENCRIPTADA ********] - {}", cadenaEncriptada);
        return customerDao.save(customer);
    }

    @Override
    public List<Customer> listCustomers() {
        return customerDao.findAll()
                .stream()
                .map(c -> {
                    c.setNombres(c.getNombres().toUpperCase());
                    c.setApellidos(c.getApellidos().toUpperCase());
                    c.setTipoDocumento(c.getTipoDocumento().toUpperCase());
                    c.setCodigoUnico(c.getCodigoUnico().toUpperCase());
                    return c;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Customer findByUniqueCode(String uniqueCode) {
        LOGGER.info("[PASO 1] - encriptar cadena");
        String cadenaBuscar = encriptarCadenaByUniqueCode(uniqueCode);

        LOGGER.info("[PASO 2] - obtener lista de clientes");
        Customer customerDB = customerDao.findByUniqueCode(cadenaBuscar);

        if (customerDB != null) {
            if (customerDB.getCodigoUnico().equals(cadenaBuscar)) {
                return customerDao.findByUniqueCode(cadenaBuscar);
            }
        }
        return null;
    }

    @Override
    public Customer findById(Long id) {
        return customerDao.findById(id).orElse(null);
    }

    //PASO 1: generar cadena de encriptación
    private SecretKeySpec crearClave(String uniqueCode) {
        try {
            byte[] cadenaArray = uniqueCode.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            cadenaArray = md.digest(cadenaArray);
            cadenaArray = Arrays.copyOf(cadenaArray, 16);
            SecretKeySpec skp = new SecretKeySpec(cadenaArray, "AES");

            return skp;
        } catch (Exception e) {
            return null;
        }
    }

    //PASO 2: encriptar cadena
    private String encriptarCadena(Customer customer, String uniqueCodeAEncriptar) {

        try {
            LOGGER.info("[PASO 1] - generar cadena de encriptación");
            SecretKeySpec claveDeEncriptacion = crearClave(customer.getCodigoUnico());

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, claveDeEncriptacion);
            byte[] cadenaAEncriptadaBytes = uniqueCodeAEncriptar.getBytes(StandardCharsets.UTF_8);

            LOGGER.info("[PASO 2] - generar cadena encriptada");
            byte[] cadenaEncriptadaBytes = cipher.doFinal(cadenaAEncriptadaBytes);
            String cadenaEncriptadaResponse = Base64.getEncoder().encodeToString(cadenaEncriptadaBytes);

            return cadenaEncriptadaResponse;
        } catch (Exception e) {
            return "";
        }
    }

    //PASO 3: desencriptar cadena para uniqueCode

    private String desencriptarCadena(Customer customer, String uniqueCodeADesencriptar) {
        try {
            SecretKeySpec claveDeEncriptacion = crearClave(customer.getCodigoUnico());
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, claveDeEncriptacion);

            byte[] cadenaADesencriptadaBytes = uniqueCodeADesencriptar.getBytes(StandardCharsets.UTF_8);
            byte[] cadenaDesencriptadaBytes = cipher.doFinal(cadenaADesencriptadaBytes);
            String cadenaDesencriptadaResponse = new String(cadenaDesencriptadaBytes);

            return cadenaDesencriptadaResponse;
        } catch (Exception e) {
            return "";
        }
    }

    private String encriptarCadenaByUniqueCode(String uniqueCodeAEncriptar) {

        try {
            LOGGER.info("[PASO 1] - generar cadena de encriptación");
            SecretKeySpec claveDeEncriptacion = crearClave(uniqueCodeAEncriptar);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, claveDeEncriptacion);
            byte[] cadenaAEncriptadaBytes = uniqueCodeAEncriptar.getBytes(StandardCharsets.UTF_8);

            LOGGER.info("[PASO 2] - cadena encriptada");
            byte[] cadenaEncriptadaBytes = cipher.doFinal(cadenaAEncriptadaBytes);
            String cadenaEncriptadaResponse = Base64.getEncoder().encodeToString(cadenaEncriptadaBytes);

            return cadenaEncriptadaResponse;
        } catch (Exception e) {
            return "";
        }
    }
}
