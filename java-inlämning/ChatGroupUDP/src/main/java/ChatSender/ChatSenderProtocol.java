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
            System.out.println("Failed to create chatSenderMulticast");
        }

        chatSenderMulticast.start();
    }

    public void runProtocol(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        Object object = null;

        System.out.println("In protocol: " + jsonString);

        try {
            JsonNode root = mapper.readTree(jsonString);

            // CHECK IF USER
            if(root.has("userName")) {
                User newUser = mapper.readValue(jsonString, User.class);

                // CHECK IF USER IS ACTIVE
                if(newUser.isActive()) {
                    // IF USER DON'T EXIST - ADD TO LIST AND SEND NEW LIST TO RECEIVERS
                    if(!currentUsers.getUsers().contains(newUser)) {
                        currentUsers.addUser(newUser);
                        object = currentUsers;
                        System.out.println("Sending new users");
                    }
                }
                // IF USER IS INACTIVATED - REMOVE FROM LIST
                else {
                    currentUsers.removeUser(newUser);
                    object = newUser;
                    System.out.println(newUser.getUserName());
                }
            }
            else if(root.has("message") && root.has("date")) {
                Message newMessage = mapper.readValue(jsonString, Message.class);
                messageHistory.addMessage(newMessage);
                object = newMessage;
            }

        } catch (JsonProcessingException e) {
            System.out.println("Unable to map json");
        }

        if(object != null) {
            chatSenderMulticast.send(object);
        }
    }
}
