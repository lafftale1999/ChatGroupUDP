package User;

import java.util.ArrayList;

public class Users {
    final private ArrayList<User> users = new ArrayList<>();

    public void addUser(User user) {
        if(user != null) {
            users.add(user);
        }
    }

    public void removeUser(User user) {
        if(user != null) {
            users.remove(user);
        }
    }
}
