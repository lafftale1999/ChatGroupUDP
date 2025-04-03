package ChatReceiver;

import GUI.ChatWindow;
import Message.Message;
import User.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

public class ChatReceiverSender implements ActionListener {

    // Access to GUI for ActionListeners
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

        // Send can also be invoked by pressing Enter.
        // To add a new line you can also use shift + Enter.
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

    public void sendHandShake() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(currentUser);
            byte[] userBytes = json.getBytes();
            DatagramPacket userPacket = new DatagramPacket(userBytes, userBytes.length, serverAddress, toPort);

            socket.send(userPacket);
            System.out.println("Shaking server's hand!");
        } catch (JsonProcessingException e) {
            System.out.println("ChatReceiverSender sendHandShake() - Unable to process JSON: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("ChatReceiverSender sendHandShake() - IOException: " + e.getMessage());
        }
    }

    public void sendMessage() {
        if(!chatWindow.getSendMessagePanel().getMessageTextArea().getText().isEmpty()) {
            // Get message from GUI
            Message message = new Message(chatWindow.getSendMessagePanel().getMessageTextArea().getText(), currentUser);

            ObjectMapper mapper = new ObjectMapper();
            String json = null;

            try {
                json = mapper.writeValueAsString(message);
                if(json != null) {
                    byte[] bytes = json.getBytes();
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, toPort);
                    socket.send(packet);
                }
            } catch (IOException e) {
                System.out.println("ChatReceiverSender sendMessage() - IOException: " + e.getMessage());
            }

            chatWindow.getSendMessagePanel().getMessageTextArea().setText("");
        }
        else {
            JOptionPane.showMessageDialog(chatWindow, "You must enter something before sending!");
        }
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
            socket.send(messagePacket);

        } catch (IOException e) {
            System.out.println("ChatReceiverSender sendLastBreath() - IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("ChatReceiverSender sendLastBreath() - Interrupted Exception: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}
