package ChatSender;

import Message.Message;
import User.User;
import User.Users;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatSenderMulticast extends Thread{

    BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    int toPort = 55554;
    MulticastSocket socket = new MulticastSocket();
    InetAddress multiCastAddress;

    public ChatSenderMulticast() throws IOException {
        multiCastAddress = InetAddress.getByName("234.234.234.234");
    }

    public synchronized void send(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            if(object instanceof Message message) {
                json = mapper.writeValueAsString(message);
            }
            else if(object instanceof User user) {
                json = mapper.writeValueAsString(user);
            }
            else if(object instanceof Users users) {
                json = mapper.writeValueAsString(users);
            }
        }catch (IOException e) {
            System.out.println("ChatSenderMulticast.java send() - could not cast object");
        }

        if(json != null) {
            String finalJson = json;
            addTask(() -> sendObject(finalJson));
        }
    }

    public void sendObject(String json) {
        byte[] bytes = json.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, multiCastAddress, toPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTask(Runnable task) {
        this.tasks.add(task);
    }

    @Override
    public void run() {
        try {
            while(!this.isInterrupted()) {
                Runnable task = tasks.take();
                task.run();
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted, ending program");
        } finally {
            socket.close();
        }
    }
}
