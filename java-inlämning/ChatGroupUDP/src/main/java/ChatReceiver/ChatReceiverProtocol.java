package ChatReceiver;

import GUI.ChatWindow;
import Message.Message;
import User.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatReceiverProtocol implements ActionListener {

    private enum States {
        FIRST_LOG_IN,
        READY_TO_RECEIVE,
        LOG_OUT
    }

    States state = States.FIRST_LOG_IN;
    ChatWindow chatWindow;

    public ChatReceiverProtocol(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
        this.createListeners();
    }

    public void runProtocol(Object object) {
        if(state == States.FIRST_LOG_IN) {
            // Get current users
            state = States.READY_TO_RECEIVE;
        }
        else if (state == States.READY_TO_RECEIVE) {
            // received a message
            if(object instanceof User user) {

            }
            else if (object instanceof Message message) {

            }
        }
        else if (state == States.LOG_OUT) {

        }
        else {

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    public void createListeners() {
        chatWindow.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Logging out!");
            }
        });

        chatWindow.getSendMessagePanel().getSendButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sending message");
            }
        });
    }

}
