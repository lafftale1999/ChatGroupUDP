package GUI.ChatArea;

import User.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserTextArea extends JTextArea {
    JScrollPane scrollPane = new JScrollPane(this);
    ArrayList<String> users = new ArrayList<>();

    public UserTextArea() {
        this.setEditable(false);
        this.setLineWrap(true);
        this.setCaretColor(Color.WHITE);
    }

    public void addUser(User user) {
        users.add(user.getUserName());
        this.draw();
    }

    public void draw(){
        this.setText("");

        for(String user: users) {
            this.append(user + "\n");
        }

        this.setCaretPosition(this.getDocument().getLength());
        this.revalidate();
        this.repaint();
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
