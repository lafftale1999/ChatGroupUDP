package ChatSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ChatSenderListener{
    ChatSenderProtocol protocol = new ChatSenderProtocol();
    int myPort = 55555;

    public ChatSenderListener() {
        try(DatagramSocket socket = new DatagramSocket(myPort)) {
            while(true) {
                byte[] bytes = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);

                String json = new String(packet.getData(), 0, packet.getLength());
                protocol.runProtocol(json);
            }
        } catch (SocketException e) {
            System.out.println("ChatSenderListener Constructor() - Socket closed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("ChatSenderListener Constructor() - failed to read from socket: " + e.getMessage());
        }
    }
}
