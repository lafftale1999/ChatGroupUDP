package ChatSender;

import Message.Message;
import Message.Messages;
import User.User;
import User.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ChatSenderProtocol {
    Messages messageHistory = new Messages();
    Users currentUsers = new Users();
    ChatSenderMulticast chatSenderMulticast;

    public ChatSenderProtocol() {
        try {
            chatSenderMulticast = new ChatSenderMulticast();
        } catch (IOException e) {
            System.out.println("ChatSenderProtocol Constructor() - Failed to create ChatSenderMulticast: " + e.getMessage());
        }
        chatSenderMulticast.start();
    }

    public void runProtocol(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        Object object = null;

        try {
            JsonNode root = mapper.readTree(jsonString);

            // IF USER
            if(root.has("userName") && !root.has("message")) {
                User newUser = mapper.readValue(jsonString, User.class);

                // Is user active?
                if(newUser.isActive()) {
                    // Does user exist in currentUsers? If not - add and send out!
                    if(!currentUsers.getUsers().contains(newUser)) {
                        currentUsers.addUser(newUser);
                        object = currentUsers;
                    }
                }
                // If user de-activated, remove from list and out user
                else {
                    currentUsers.removeUser(newUser);
                    object = newUser;
                }
            }
            // IF MESSAGE
            else if(root.has("message") && root.has("date")) {
                Message newMessage = mapper.readValue(jsonString, Message.class);
                messageHistory.addMessage(newMessage);
                object = newMessage;
            }

        } catch (JsonProcessingException e) {
            System.out.println("ChatSenderProtocol runProtocol() - Unable to process JSON: " + e. getMessage());
        }

        if(object != null) {
            chatSenderMulticast.send(object);
        }
    }
}
