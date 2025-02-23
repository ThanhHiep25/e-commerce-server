package org.server.controllers;

import org.server.entities.Cart;
import org.server.services.CartService;
import jakarta.persistence.EntityManager;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CartController implements Runnable {
    private final Socket clientSocket;
    private final CartService cartService;

    public CartController(Socket clientSocket, EntityManager entityManager) {
        this.clientSocket = clientSocket;
        this.cartService = new CartService(entityManager);
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String action = input.readUTF();
            switch (action) {
                case "ADD_TO_CART":
                    Cart cart = (Cart) input.readObject();
                    cartService.save(cart);
                    output.writeUTF("SUCCESS");
                    break;

                case "GET_CART":
                    Long cartId = input.readLong();
                    Cart foundCart = cartService.findById(cartId);
                    output.writeObject(foundCart);
                    break;

                case "REMOVE_CART":
                    Long removeId = input.readLong();
                    cartService.delete(removeId);
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
