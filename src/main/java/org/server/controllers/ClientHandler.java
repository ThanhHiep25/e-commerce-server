package org.server.controllers;

import java.io.*;
import java.net.Socket;
import javax.swing.JTextArea;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private JTextArea logArea;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isRunning = true;

    public ClientHandler(Socket socket, JTextArea logArea) {
        this.clientSocket = socket;
        this.logArea = logArea;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            log("Lỗi khi tạo luồng dữ liệu: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            log("Client đã kết nối: " + clientSocket.getInetAddress().getHostAddress());

            String request;
            while (isRunning && (request = reader.readLine()) != null) {
                log("Nhận từ client: " + request);
                String response = handleRequest(request);
                writer.println(response);
            }
        } catch (IOException e) {
            log("Lỗi giao tiếp với client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private String handleRequest(String request) {
        // Xử lý yêu cầu từ Client
        switch (request) {
            case "PING":
                return "PONG";
            case "HELLO":
                return "Xin chào từ Server!";
            default:
                return "Lệnh không hợp lệ!";
        }
    }

    public void closeConnection() {
        isRunning = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (clientSocket != null) clientSocket.close();
            log("Client đã ngắt kết nối.");
        } catch (IOException e) {
            log("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }
}
