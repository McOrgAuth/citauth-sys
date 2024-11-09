package io.github.mam1zu.connection;

import io.github.mam1zu.instruction.*;
import io.github.mam1zu.instruction.instructionresult.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

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

            StringBuilder req = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(this.is, StandardCharsets.UTF_8);
            int data_tmp;
            while(true) {
                data_tmp = isr.read();
                req.append((char)data_tmp);
                if(data_tmp == '}') break;
            }
            System.out.println(req);
            JSONObject req_json = new JSONObject(req.toString());

            if(req_json.getString("hello").equals("ping")) {
                System.out.println("true?");
                JSONObject res_json = new JSONObject();
                res_json.append("hello","pong");
                this.os.write(res_json.toString().getBytes());
                return true;
            }

            /*
            if(req.toString().equals("HELLO_CITAUTH_SYS")) {
                this.os.write("HELLO_CITAUTH_API\n".getBytes());
                return true;
            }
            */

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

        if(this.socket == null) {
            return null;
        }
        if(this.is == null) {
            this.is = socket.getInputStream();
        }
        if(this.os == null) {
            this.os = socket.getOutputStream();
        }

        StringBuilder inst_tmp = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(this.is, StandardCharsets.UTF_8);

        int data_tmp;
        while(true) {
            data_tmp = isr.read();
            if(data_tmp == '\0') resetApiConnection();
            inst_tmp.append((char)data_tmp);
            if(data_tmp == '}') break;
        }

        String inst = inst_tmp.toString();

        JSONObject inst_json = new JSONObject(inst);

        if(!inst_json.isNull("bye")) {
            JSONObject res_json = new JSONObject();
            res_json.put("bye", "pong");

            this.os.write(res_json.toString().getBytes());
            return new Goodbye(null);
        }

        if(inst_json.getString("method").equals("PRRG")) {
            String email = inst_json.getString("email");
            String uuid = inst_json.getString("uuid");
            return new PreRegisterUser(uuid, email);
        }
        else if(inst_json.getString("method").equals("AUTH")) {
            String uuid = inst_json.getString("uuid");
            return new AuthenticateUser(uuid);
        }
        else if(inst_json.getString("method").equals("RGST")) {
            String preregid = inst_json.getString("preregid");
            return new RegisterUser(null, null, preregid);
        }
        else if(inst_json.getString("method").equals("DELT")) {
            String uuid = inst_json.getString("uuid");
            String email = inst_json.getString("email");
            return new DeleteUser(uuid, email);
        }

        return null;


    }

    public Instruction getInstruction_old() throws IOException {

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
        
        if(ar.getResult() == 1) {
            System.out.println("AUTH_SUCCESS:"+ ar.getUUID());
        }
        else {
            System.out.println("AUTH_FAIL:"+ ar.getUUID());
        }

        JSONObject res_json = new JSONObject();
        res_json.put("method", "AUTH");
        res_json.put("uuid", ar.getUUID());
        res_json.put("status", ar.getResult());
        this.os.write(res_json.toString().getBytes());

        return true;

    }

    public boolean returnResult(PreRegisterResult pr) throws IOException {

        if(!(this.checkCon() && this.checkStreams()))
            return false;

        if(pr.getResult() == 1) {
            System.out.println("PREREGISTER_SUCCESS:"+ pr.getUUID());
        }
        else {
            System.out.println("PREREGISTER_FAIL:"+ pr.getUUID());
        }
        JSONObject res_json = new JSONObject();

        res_json.put("method", "PRRG");
        res_json.put("email", pr.getEmail());
        res_json.put("uuid", pr.getUUID());
        res_json.put("status", pr.getResult());
        if(pr.getResult() == 1) res_json.put("preregid", pr.getPreregid());

        this.os.write(res_json.toString().getBytes());

        return true;
    }

    public boolean returnResult(RegisterResult rr) throws IOException {

        if(!(this.checkCon() && this.checkStreams()))
            return false;

        JSONObject res_json = new JSONObject();
        res_json.put("method", "RGST");
        res_json.put("preregid", rr.getPreregid());
        res_json.put("status", rr.getResult());
        this.os.write(res_json.toString().getBytes());

        return true;
    }

    public boolean returnResult(DeleteResult dr) throws IOException {
        if(!(this.checkCon() && this.checkStreams()))
            return false;
        
        JSONObject res_json = new JSONObject();
        res_json.put("method", "DELT");
        res_json.put("email", dr.getEmail());
        res_json.put("uuid", dr.getUUID());
        res_json.put("status", dr.getResult());
        this.os.write(res_json.toString().getBytes());
        
        return true;
    }

    void resetApiConnection() {
        System.out.println("CITAUTH-API seems down");
    }
}
