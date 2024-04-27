package io.github.mam1zu.connection;

import io.github.mam1zu.instruction.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public final class APIConnection extends AccessConnection {
    private Socket socket = null;
    private OutputStream os = null;
    private InputStream is = null;
    private int port;
    private String ipAddr;
    public APIConnection(String ipAddr, int port) {
        this.ipAddr = ipAddr;
        this.port = port;
    }

    @Override
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

            this.os = socket.getOutputStream();
            this.is = socket.getInputStream();

            this.os.write("HELLO_API_SERVER\n".getBytes());

            StringBuilder res = new StringBuilder();

            InputStreamReader isr = new InputStreamReader(this.is, StandardCharsets.UTF_8);
            int data_tmp;
            while((data_tmp = isr.read()) != '\n') {
                res.append((char)data_tmp);
            }

            return res.toString().equals("HELLO_PROCESS_SERVER");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void disconnect() {
        if(!this.socket.isClosed()) {
            try {
                this.socket.close();
                System.out.println("disconnected correctly");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Instruction getInstruction() throws IOException {

        if(this.socket == null)
            return null;
        if(this.is == null) {
            this.is = socket.getInputStream();
        }

        if(this.os == null) {
            this.os = socket.getOutputStream();
        }

        StringBuilder inst_tmp = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(this.is, StandardCharsets.UTF_8);

        int data_tmp;
        while((data_tmp = isr.read()) != '\n') {
                inst_tmp.append((char)data_tmp);
        }

        String inst = inst_tmp.toString();
        int index_mcidstarts = inst.indexOf(':')+1;
        String mcid = inst.substring(index_mcidstarts, inst.length()-1);
        System.out.println(mcid);

        if(inst.startsWith("AUTHENTICATEUSER:")) {
            return new AuthenticateUser(mcid);
        }
        else if(inst.startsWith("REGISTERUSER:")) {
            return new RegisterUser(mcid);
        }
        else if(inst.startsWith("REMOVEUSER:")) {
            return new RemoveUser(mcid);
        }
        else if(inst.startsWith("PREREGISTERUSER:")) {
            return new PreRegisterUser(mcid);
        }

        return null;
    }

    public boolean returnResult(boolean result) throws IOException {
        if(this.socket == null)
            return false;
        if(this.is == null) {
            this.is = socket.getInputStream();
        }

        if(this.os == null) {
            this.os = socket.getOutputStream();
        }

        this.os.write( ((result ? "true" : "false")+'\n').getBytes() );
        return true;
    }
}
