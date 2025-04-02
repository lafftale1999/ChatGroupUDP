package ChatReceiver;

import GUI.ChatWindow;
import User.User;

import java.io.IOException;
import java.net.*;

public class ChatReceiverListener extends Thread{

    ChatWindow chatWindow;
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
        byte[] bytes = new byte[1024];

        while(!this.isInterrupted()) {
            try {
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);

                String debug = new String(packet.getData(),0,packet.getLength());
                System.out.println(debug);

                String json = new String(packet.getData(), 0, packet.getLength());
                protocol.runProtocol(json);

            } catch (SocketException socketException) {
                System.out.println("Socket closed - ending thread");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUpSocket() {
        try {
            socket = new MulticastSocket(myPort);
            InetAddress serverIp = InetAddress.getByName(serverAddressString);
            InetSocketAddress group = new InetSocketAddress(serverIp, myPort);
            NetworkInterface nwi = NetworkInterface.getByName("wlan5");

            socket.joinGroup(group, nwi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
