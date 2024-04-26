package io.github.mam1zu;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public final class APIConnection {
    Socket socket = null;
    OutputStream os = null;
    int port;
    String ipAddr;
    APIConnection(String ipAddr, int port) {
        this.ipAddr = ipAddr;
        this.port = port;

    }

    public boolean connect() {
        HttpURLConnection con = null;
        InetAddress serverIp;

        try {
            serverIp = InetAddress.getByName(ipAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        try {
            socket = new Socket(serverIp, port);

            os = socket.getOutputStream();

            os.write("HELLO_API_SERVER\n".getBytes());

            StringBuilder res = new StringBuilder();

            InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            int data_tmp;
            while((data_tmp = isr.read()) != '\n') {
                res.append((char)data_tmp);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
