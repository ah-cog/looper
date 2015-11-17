package computer.clay.looper;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Communication {

    // TODO: Create incoming message queue.
    // TODO: Create outgoing message queue.

    // TODO: startUdpServer

    public void sendDatagram (String ipAddress, String message) {
        try {
            // Send UDP packet to the specified address.
            String messageStr = message; // "turn light 1 on";
            int local_port = 4446;
            int server_port = 4445;
            DatagramSocket s = new DatagramSocket(local_port);
            InetAddress local = InetAddress.getByName(ipAddress); // ("192.168.43.235");
//                InetAddress local = InetAddress.getByName("255.255.255.255");
            int msg_length = messageStr.length();
            byte[] messageBytes = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(messageBytes, msg_length, local, server_port);
            s.send(p);
            s.close();
        } catch (IOException e) {
            Log.e("Clay", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

//            return null;
        }
    }

    // TODO: startHttpServer

    // TODO: sendHttpRequest
}
