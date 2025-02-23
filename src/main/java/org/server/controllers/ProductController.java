package org.server.controllers;

import org.server.entities.Product;
import org.server.services.ProductService;
import jakarta.persistence.EntityManager;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ProductController implements Runnable {
    private final Socket clientSocket;
    private final ProductService productService;

    public ProductController(Socket clientSocket, EntityManager entityManager) {
        this.clientSocket = clientSocket;
        this.productService = new ProductService(entityManager);
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String action = input.readUTF();
            switch (action) {
                case "ADD_PRODUCT":
                    Product product = (Product) input.readObject();
                    productService.save(product);
                    output.writeUTF("SUCCESS");
                    break;

                case "GET_PRODUCTS":
                    List<Product> products = productService.findAll();
                    output.writeObject(products);
                    break;

                case "DELETE_PRODUCT":
                    Long id = input.readLong();
                    productService.delete(id);
                    output.writeUTF("SUCCESS");
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
