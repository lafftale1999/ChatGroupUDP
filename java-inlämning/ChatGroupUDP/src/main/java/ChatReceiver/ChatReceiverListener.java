package ChatReceiver;

import GUI.ChatWindow;
import User.User;

import java.io.IOException;
import java.net.*;

public class ChatReceiverListener extends Thread{
    // Reference to current ChatWindow to update with incoming data
    ChatWindow chatWindow;

    // Protocol
    ChatReceiverProtocol protocol;
    int myPort = 55554;

    String serverAddressString = "234.234.234.234";
    MulticastSocket socket;

    public ChatReceiverListener(ChatWindow chatWindow, User currentUser) {
        this.chatWindow = chatWindow;
        this.protocol = new ChatReceiverProtocol(chatWindow, currentUser);
        this.setUpSocket();
    }

    @Override
    public void run() {
        byte[] bytes = new byte[4096];

        // While the thread is running - receive packets and run them through the protocol
        while(!this.isInterrupted()) {
            try {
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                String json = new String(packet.getData(), 0, packet.getLength());
                protocol.runProtocol(json);
            } catch (IOException e) {
                System.out.println("ChatReceiverListener.java run() - Socket closed, ending thread: " + e.getMessage());
                break;
            }
        }
    }

    public void setUpSocket() {
        try {
            socket = new MulticastSocket(myPort);
            InetAddress serverIp = InetAddress.getByName(serverAddressString);
            InetSocketAddress group = new InetSocketAddress(serverIp, myPort);
            NetworkInterface nwi = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            socket.joinGroup(group, nwi);
        } catch (IOException e) {
            System.out.println("ChatReceiverListener.java setUpSocket() - Socket closed, ending thread: " + e.getMessage());
        }
    }
}
