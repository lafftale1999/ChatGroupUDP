package ChatReceiver;

import GUI.ChatWindow;
import User.User;

import javax.swing.*;

public class ChatReceiver {
    User currentUser;
    ChatWindow chatWindow;

    public ChatReceiver() {
        while(true) {
            String userName = JOptionPane.showInputDialog("What is your username?");
            if(userName != null && !userName.isEmpty()) {
                currentUser = new User(userName);
                chatWindow = new ChatWindow(userName);
                System.out.println("Username is: " + userName);
                break;
            }
        }
    }

    public static void main(String[] args) {
        ChatReceiver chatReceiver = new ChatReceiver();
        ChatReceiverListener chatReceiverListener = new ChatReceiverListener(chatReceiver.chatWindow, chatReceiver.currentUser);
        chatReceiverListener.start();
        ChatReceiverSender chatReceiverSender = new ChatReceiverSender(chatReceiver.chatWindow, chatReceiver.currentUser);

    }
}
