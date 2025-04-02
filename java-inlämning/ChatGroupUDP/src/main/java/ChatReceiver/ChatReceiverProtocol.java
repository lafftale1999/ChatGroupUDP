package ChatReceiver;

import GUI.ChatWindow;
import Message.Message;
import User.User;
import User.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
            System.out.println("Ready to receive: " + jsonString);
        }
        else if (state == States.INITIALIZE) {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("Outside INITIALIZE: " + jsonString);

            if(jsonString.contains("\"users\"") && !jsonString.contains("\"message\"")) {
                System.out.println("Inside INITIALIZE: " + jsonString);
                try {
                    Users users = mapper.readValue(jsonString, Users.class);
                    chatWindow.getChatAreaPanel().getUserTextArea().initializeUsers(users);
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
            // USER
            if(jsonString.contains("\"userName\"") && !jsonString.contains("\"message\"")) {
                User user = mapper.readValue(jsonString, User.class);
                // IF USER IS ACTIVE AND NOT IN THE USER LIST - ADD TO USER LIST
                if (user.isActive() && !chatWindow.getChatAreaPanel().getUserTextArea().getUsers().contains(user.getUserName())) {
                    chatWindow.getChatAreaPanel().getUserTextArea().addUser(user);
                }
                else if(!user.isActive()) {
                    chatWindow.getChatAreaPanel().getUserTextArea().removeUser(user);
                }
            }
            // MESSAGE
            else if (jsonString.contains("\"message\"") && jsonString.contains("\"date\"")) {
                // MESSAGE STUFF
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
