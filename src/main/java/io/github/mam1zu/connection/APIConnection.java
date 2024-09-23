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

        int invalid_char_counter = 0;

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
            if(data_tmp == '\0') resetApiConnection();
            inst_tmp.append((char)data_tmp);
        }

        String inst = inst_tmp.toString();

        //only bye is processed to avoid latter string process
        if(inst.equals("BYE_CITAUTH_SYS")) {
            this.os.write("BYE_CITAUTH_API\n".getBytes());
            return new Goodbye(null);
        }


        //get email address if exists
        String email = null;
        if(inst.contains("|")) {
            int index_emailstarts = inst.indexOf('|')+1;
            email = inst.substring(index_emailstarts);
        }

        //get uuid
        int index_uuidstarts = inst.indexOf(':')+1;
        String uuid = inst.substring(index_uuidstarts, index_uuidstarts+32);//UUID length is basically 32

        if(inst.startsWith("AUTH:")) {
            return new AuthenticateUser(uuid);
        }
        else if(inst.startsWith("RGST:")) {
            //get pre-registration id
            int index_preregidstarts = inst.indexOf('#')+1;
            String preregid = inst.substring(index_preregidstarts, index_preregidstarts+32);
            return new RegisterUser(uuid, email, preregid);
        }
        else if(inst.startsWith("DELT:")) {
            return new DeleteUser(uuid, email);
        }
        else if(inst.startsWith("PRRG:")) {
            return new PreRegisterUser(uuid, email);
        }

        return null;
    }

    public boolean returnResult(AuthenticateResult ar) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(ar.getResult()) {
            System.out.println("AUTH_SUCCESS:"+ ar.getUUID());
            this.os.write(("AUTH_SUCCESS:"+ ar.getUUID() +'\n').getBytes());
        }
        else {
            System.out.println("AUTH_FAIL:"+ ar.getUUID());
            this.os.write(("AUTH_FAIL:"+ ar.getUUID() + '\n').getBytes());
        }

        return true;
    }

    public boolean returnResult(PreRegisterResult pr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(pr.getResult())
            this.os.write(("PRRG_SUCCESS:"+pr.getUUID()+'|'+pr.getEmail()+'\n').getBytes());
        else
            this.os.write(("PRRG_FAIL:"+pr.getUUID()+'|'+pr.getEmail()+'\n').getBytes());
        return true;
    }

    public boolean returnResult(RegisterResult rr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(rr.getResult())
            this.os.write(("RGST_SUCCESS:"+rr.getUUID()+ '|' + rr.getEmail() + '\n').getBytes());
        else
            this.os.write(("RGST_FAIL:"+rr.getUUID()+ '|' + rr.getEmail() + '\n').getBytes());

        return true;
    }

    public boolean returnResult(DeleteResult rr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(rr.getResult())
            this.os.write(("DELT_SUCCESS:"+rr.getUUID()+'\n').getBytes());
        else
            this.os.write(("DELT_FAIL:"+rr.getUUID()+'\n').getBytes());

        return true;
    }

    void resetApiConnection() {
        System.out.println("CITAUTH-API seems down");
    }
}
