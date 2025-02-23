package org.server.controllers;

import org.server.entities.Order;
import org.server.services.OrderService;
import jakarta.persistence.EntityManager;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class OrderController implements Runnable {
    private final Socket clientSocket;
    private final OrderService orderService;

    public OrderController(Socket clientSocket, EntityManager entityManager) {
        this.clientSocket = clientSocket;
        this.orderService = new OrderService(entityManager);
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String action = input.readUTF();
            switch (action) {
                case "CREATE_ORDER":
                    Order order = (Order) input.readObject();
                    orderService.save(order);
                    output.writeUTF("SUCCESS");
                    break;

                case "GET_ORDERS":
                    List<Order> orders = orderService.findAll();
                    output.writeObject(orders);
                    break;

                case "DELETE_ORDER":
                    Long id = input.readLong();
                    orderService.delete(id);
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
