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

    private enum States {
        INITIALIZE,
        READY_TO_RECEIVE
    }

    States state = States.INITIALIZE;
    ChatWindow chatWindow;
    User currentUser;

    public ChatReceiverProtocol(ChatWindow chatWindow, User currentUser) {
        this.chatWindow = chatWindow;
        this.currentUser = currentUser;
    }

    public void runProtocol(String jsonString) {
        if (state == States.READY_TO_RECEIVE) {
            this.doAction(jsonString);
        }
        else if (state == States.INITIALIZE) {
            ObjectMapper mapper = new ObjectMapper();

            if(jsonString.contains("\"users\"") && !jsonString.contains("\"message\"")) {
                try {
                    Users users = mapper.readValue(jsonString, Users.class);
                    chatWindow.getChatAreaPanel().getUserTextArea().updateUsers(users);
                    state = States.READY_TO_RECEIVE;
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doAction(String jsonString) {
        // received a message
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode root = mapper.readTree(jsonString);

            if(root.has("users")) {
                Users users = mapper.readValue(jsonString, Users.class);
                chatWindow.getChatAreaPanel().getUserTextArea().updateUsers(users);
            }
            else if(root.has("user") && !root.has("message")) {
                User user = mapper.readValue(jsonString, User.class);
                // IF USER IS ACTIVE AND NOT IN THE USER LIST - ADD TO USER LIST
                if (user.isActive() && !chatWindow.getChatAreaPanel().getUserTextArea().getUsers().contains(user.getUserName())) {
                    chatWindow.getChatAreaPanel().getUserTextArea().addUser(user);

                    // DEBUG
                    System.out.println("User added");

                }
                else if(!user.isActive()) {
                    chatWindow.getChatAreaPanel().getUserTextArea().removeUser(user);
                }
            }
            else if(root.has("message") && root.has("date")) {
                System.out.println("First in doAction");
                Message message = mapper.readValue(jsonString, Message.class);
                if(message != null) {
                    System.out.println("Second in doAction");
                    chatWindow.getChatAreaPanel().getChatTextArea().addMessage(message);
                }
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
