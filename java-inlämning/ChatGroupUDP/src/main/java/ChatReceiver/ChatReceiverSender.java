package ChatReceiver;

import GUI.ChatWindow;
import Message.Message;
import User.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

public class ChatReceiverSender implements ActionListener {

    ChatWindow chatWindow;

    User currentUser;
    int toPort = 55555;
    InetAddress serverAddress;
    DatagramSocket socket;

    public ChatReceiverSender(ChatWindow chatWindow, User currentUser) {
        this.chatWindow = chatWindow;
        this.currentUser = currentUser;
        this.createListeners();
        try {
            serverAddress = InetAddress.getLocalHost();
            socket = new DatagramSocket();
        } catch (IOException e) {
            System.out.println("Error instantiating ChatReceiverSender!");
        }

        this.sendHandShake();
    }

    public void createListeners() {
        chatWindow.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentUser.setActive(false);
                sendLastBreath();
                System.exit(0);
            }
        });

        chatWindow.getSendMessagePanel().getSendButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JTextArea inputArea = chatWindow.getSendMessagePanel().getMessageTextArea();

        inputArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendOnEnter");
        inputArea.getActionMap().put("sendOnEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
    }

    public void sendLastBreath() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(currentUser);
            byte[] bytes = json.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, toPort);
            socket.send(packet);

            Thread.sleep(50);

            Message message = new Message(currentUser.getUserName() + " has left the chat", currentUser);
            json = mapper.writeValueAsString(message);
            bytes = json.getBytes();
            DatagramPacket messagePacket = new DatagramPacket(bytes, bytes.length, serverAddress,toPort);
            socket.send(packet);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage() {
        if(!chatWindow.getSendMessagePanel().getMessageTextArea().getText().isEmpty()) {
            Message message = new Message(chatWindow.getSendMessagePanel().getMessageTextArea().getText(), currentUser);

            ObjectMapper mapper = new ObjectMapper();
            String json = null;

            try {
                json = mapper.writeValueAsString(message);
                byte[] bytes = json.getBytes();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, toPort);
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            chatWindow.getSendMessagePanel().getMessageTextArea().setText("");
        }
        else {
            JOptionPane.showMessageDialog(chatWindow, "You must enter something before sending!");
        }
    }

    public void sendHandShake() {
        try {
            // CREATE USER PACKET
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(currentUser);
            byte[] userBytes = json.getBytes();
            DatagramPacket userPacket = new DatagramPacket(userBytes, userBytes.length, serverAddress, toPort);

            // SEND PACKETS
            socket.send(userPacket);
            System.out.println("Handshake sent");
        } catch (JsonProcessingException e) {
            System.out.println("Error mapping currentUser as json");
        } catch (IOException e) {
            System.out.println("Unknown error");
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {}
}
