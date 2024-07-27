package io.github.mam1zu.connection;

import io.github.mam1zu.instruction.*;
import io.github.mam1zu.instruction.instructionresult.*;

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
        try (ServerSocket ssocket = new ServerSocket(this.port)){

            socket = ssocket.accept();
            this.is = socket.getInputStream();
            this.os = socket.getOutputStream();

            StringBuilder res = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(this.is, StandardCharsets.UTF_8);
            int data_tmp;
            while((data_tmp = isr.read()) != '\n') {
                res.append((char)data_tmp);
            }

            if(res.toString().equals("HELLO_CITAUTH_SYS")) {
                this.os.write("HELLO_CITAUTH_API\n".getBytes());
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public boolean connect_legacy() {
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

            this.os.write("HELLO_CITAUTH_API\n".getBytes());

            StringBuilder res = new StringBuilder();

            InputStreamReader isr = new InputStreamReader(this.is, StandardCharsets.UTF_8);
            int data_tmp;
            while((data_tmp = isr.read()) != '\n') {
                res.append((char)data_tmp);
            }

            return res.toString().equals("HELLO_CITAUTH_SYS");

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

    public boolean checkCon() {
        return this.socket != null && !this.socket.isClosed();
    }

    public boolean checkStreams() {
        return this.is != null && this.os != null;
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
        String mcid = inst.substring(index_mcidstarts);

        if(inst.startsWith("AUTH:")) {
            return new AuthenticateUser(mcid);
        }
        else if(inst.startsWith("RGST:")) {
            return new RegisterUser(mcid);
        }
        else if(inst.startsWith("DELT:")) {
            return new RemoveUser(mcid);
        }
        else if(inst.startsWith("PRRG:")) {
            return new PreRegisterUser(mcid);
        }
        else if(inst.equals("BYE_CITAUTH_SYS")) {
            this.os.write("BYE_CITAUTH_API\n".getBytes());
            return new Goodbye(null);
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

    public boolean returnResult(AuthenticateResult ar) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(ar.getResult()) {
            System.out.println("AUTH_SUCCESS:"+ ar.getUUID() + '\n');
            this.os.write(("AUTH_SUCCESS:"+ ar.getUUID() + '\n').getBytes());
        }
        else {
            System.out.println("AUTH_FAIL:"+ ar.getUUID() + '\n');
            this.os.write(("AUTH_FAIL:"+ ar.getUUID() + '\n').getBytes());
        }

        return true;
    }

    public boolean returnResult(PreRegisterResult pr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(pr.getResult())
            this.os.write(("PRRG_SUCCESS:"+pr.getUUID()+'\n').getBytes());
        else
            this.os.write(("PRRG_FAIL:"+pr.getUUID()+'\n').getBytes());
        return true;
    }

    public boolean returnResult(RegisterResult rr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(rr.getResult())
            this.os.write(("RGST_SUCCESS:"+rr.getUUID()+'\n').getBytes());
        else
            this.os.write(("RGST_FAIL:"+rr.getUUID()+'\n').getBytes());

        return true;
    }

    public boolean returnResult(RemoveResult rr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(rr.getResult())
            this.os.write(("DELT_SUCCESS:"+rr.getUUID()+'\n').getBytes());
        else
            this.os.write(("DELT_FAIL:"+rr.getUUID()+'\n').getBytes());

        return true;
    }
}
