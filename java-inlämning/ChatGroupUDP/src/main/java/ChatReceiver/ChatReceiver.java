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
                break;
            }
        }
    }

    public static void main(String[] args) {
        // Creating currentUser and GUI
        ChatReceiver chatReceiver = new ChatReceiver();

        // Creating Listener for incoming packets. Also instantiates ChatReceiverProtocol that controls logic + GUI
        ChatReceiverListener chatReceiverListener = new ChatReceiverListener(chatReceiver.chatWindow, chatReceiver.currentUser);
        chatReceiverListener.start();

        // Creating a Sender to send data when actions are performed in GUI
        ChatReceiverSender chatReceiverSender = new ChatReceiverSender(chatReceiver.chatWindow, chatReceiver.currentUser);

    }
}
