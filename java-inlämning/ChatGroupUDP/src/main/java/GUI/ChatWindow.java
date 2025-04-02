package GUI;

import GUI.ChatArea.ChatAreaPanel;
import GUI.SendMessageArea.SendMessagePanel;
import GUI.StopButtonArea.StopButton;
import Message.Message;
import User.User;

import javax.swing.*;
import java.awt.*;

public class ChatWindow extends JFrame {
    StopButton stopButton = new StopButton("Log out");
    ChatAreaPanel chatAreaPanel = new ChatAreaPanel();
    SendMessagePanel sendMessagePanel = new SendMessagePanel();

    public ChatWindow(String userName) {

        ImageIcon image = new ImageIcon("src/Resources/Icon1.png");
        this.setIconImage(image.getImage());
        this.setLayout(new BorderLayout());
        this.setTitle("Group Chat" + " - " + userName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.add(stopButton, BorderLayout.NORTH);
        this.add(chatAreaPanel, BorderLayout.CENTER);
        this.add(sendMessagePanel, BorderLayout.SOUTH);
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        ChatWindow window = new ChatWindow("Carl");

        User user1 = new User("Carl Broman");
        User user2 = new User("Therese Christensen");

        window.chatAreaPanel.getUserTextArea().addUser(user1);
        window.chatAreaPanel.getUserTextArea().addUser(user2);
        window.chatAreaPanel.getChatTextArea().addMessage(new Message("Hello we are here", user1));
        window.chatAreaPanel.getChatTextArea().addMessage(new Message("And now we are here", user2));
    }

    public StopButton getStopButton() {
        return stopButton;
    }

    public ChatAreaPanel getChatAreaPanel() {
        return chatAreaPanel;
    }

    public SendMessagePanel getSendMessagePanel() {
        return sendMessagePanel;
    }
}
