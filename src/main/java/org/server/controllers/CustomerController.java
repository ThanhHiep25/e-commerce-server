package org.server.controllers;

import org.server.entities.Customer;
import org.server.services.CustomerService;
import jakarta.persistence.EntityManager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class CustomerController implements Runnable {
    private final Socket clientSocket;
    private final CustomerService customerService;

    public CustomerController(Socket clientSocket, EntityManager entityManager) {
        this.clientSocket = clientSocket;
        this.customerService = new CustomerService(entityManager);
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String action = input.readUTF();
            switch (action) {
                case "REGISTER":
                    Customer newCustomer = (Customer) input.readObject();
                    if (customerService.findByEmail(newCustomer.getEmail()).isPresent()) {
                        output.writeUTF("EMAIL_EXISTS");
                    } else {
                        customerService.save(newCustomer);
                        output.writeUTF("SUCCESS");
                    }
                    break;

                case "LOGIN":
                    String email = input.readUTF();
                    String password = input.readUTF();
                    Optional<Customer> customerOpt = customerService.findByEmail(email);

                    if (customerOpt.isPresent() && customerOpt.get().getPassword().equals(password)) {
                        output.writeUTF("SUCCESS");
                        output.writeObject(customerOpt.get());
                    } else {
                        output.writeUTF("INVALID_CREDENTIALS"); 
                    }
                    break;

                case "GET_ALL_CUSTOMERS":
                    List<Customer> customers = customerService.findAll();
                    output.writeObject(customers);
                    break;

                case "DELETE_CUSTOMER":
                    Long id = input.readLong();
                    customerService.delete(id);
                    output.writeUTF("SUCCESS");
                    break;

                case "UPDATE_CUSTOMER":
                    Customer updatedCustomer = (Customer) input.readObject();
                    Optional<Customer> existingCustomerOpt = customerService.findByEmail(updatedCustomer.getEmail());

                    if (existingCustomerOpt.isPresent()) {
                        customerService.update(updatedCustomer);
                        output.writeUTF("SUCCESS");
                    } else {
                        output.writeUTF("CUSTOMER_NOT_FOUND");
                    }
                    break;

                default:
                    output.writeUTF("INVALID_ACTION");
            }
            output.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
