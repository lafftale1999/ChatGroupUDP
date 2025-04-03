package ChatReceiver;

import GUI.ChatWindow;
import Message.Message;
import User.User;
import User.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatReceiverProtocol {

    // States for logic : first login and then participating in chat
    private enum States {
        INITIALIZE,
        READY_TO_RECEIVE
    }

    States state = States.INITIALIZE;
    // Access to be able to update GUI
    ChatWindow chatWindow;
    User currentUser;

    public ChatReceiverProtocol(ChatWindow chatWindow, User currentUser) {
        this.chatWindow = chatWindow;
        this.currentUser = currentUser;
    }

    public void runProtocol(String jsonString) {
        if (state == States.READY_TO_RECEIVE) {
            doAction(jsonString);
        }
        else if (state == States.INITIALIZE) {
            ObjectMapper mapper = new ObjectMapper();
            if(jsonString.contains("\"users\"") && !jsonString.contains("\"message\"")) {
                try {
                    Users users = mapper.readValue(jsonString, Users.class);
                    chatWindow.getChatAreaPanel().getUserTextArea().updateUsers(users);
                    System.out.println("Hand is shaken!");
                    state = States.READY_TO_RECEIVE;
                } catch (JsonProcessingException e) {
                    System.out.println("ChatReceiverProtocol runProtocol() - unable to process JSON: " + e.getMessage());
                }
            }
        }
    }

    private void doAction(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode root = mapper.readTree(jsonString);

            // IF USERS
            if(root.has("users")) {
                Users users = mapper.readValue(jsonString, Users.class);
                chatWindow.getChatAreaPanel().getUserTextArea().updateUsers(users);
            }
            // IF USER
            else if(root.has("userName") && !root.has("message")) {
                User user = mapper.readValue(jsonString, User.class);

                if (user.isActive() && !chatWindow.getChatAreaPanel().getUserTextArea().getUsers().contains(user.getUserName())) {
                    chatWindow.getChatAreaPanel().getUserTextArea().addUser(user);
                }
                else if(!user.isActive()) {
                    chatWindow.getChatAreaPanel().getUserTextArea().removeUser(user);
                }
                else {
                    System.out.println("Something went wrong when deleting users");
                }
            }
            // IF MESSAGE
            else if(root.has("message") && root.has("date")) {
                Message message = mapper.readValue(jsonString, Message.class);
                if(message != null) {
                    chatWindow.getChatAreaPanel().getChatTextArea().addMessage(message);
                }
            }
            // IF SOMETHING IS WRONG
            else {
                System.out.println("Something went wrong in ChatReceiverProtocol doAction()");
            }
        } catch (JsonProcessingException e) {
            System.out.println("ChatReceiverProtocol doAction() - unable to process JSON: " + e.getMessage());
        }
    }
}
