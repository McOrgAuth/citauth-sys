package io.github.mam1zu;

import io.github.mam1zu.connection.APIConnection;
import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.Goodbye;
import io.github.mam1zu.instruction.Instruction;
import io.github.mam1zu.instruction.instructionresult.*;
import io.github.mam1zu.utils.Config;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        System.out.println("CITAUTH-PROCESS-SYSTEM");

        loadConfig();

        checkMySQLConnection();
        establishAPIConnection();

        while(true) {
            if(!apicon.checkCon()) {
                System.out.println("Socket is already closed, exit");
                break;
            }
            else {
                if(!processInstruction()) {
                    System.out.println("API server has said goodbye to process server.");
                    System.out.println("Disconnecting connections...");
                    disconnectAllConnections();
                    System.out.println("Done. Goodbye!");
                    break;
                }
            }
        }
        disconnectAllConnections();
    }

    static APIConnection apicon = null;
    static MySQLConnection dbcon = null;
    static Config config = null;
    static HashMap<String, String> mysqlconfig = null;

    static void establishAPIConnection() {
        System.out.println("Waiting connection from API server...");
        apicon = new APIConnection("172.24.241.112", 37565);
        if(!apicon.connect()) {
            System.out.println("Connection to API failed");
            System.exit(-1);
        }
        System.out.println("Connection to API established!");
    }

    static void checkMySQLConnection() {
        System.out.println("Checking connection to database server...");
        dbcon = new MySQLConnection(mysqlconfig.get("host"), mysqlconfig.get("db"),mysqlconfig.get("user"), mysqlconfig.get("password"), mysqlconfig.get("port"));
        if(!dbcon.connect()) {
            System.out.println("Connection to DB failed");
            apicon.disconnect();
            System.exit(-1);
        }
        System.out.println("Connection to DB successed!");
    }

    static void disconnectAllConnections() {
        apicon.disconnect();
        dbcon.disconnect();
    }

    static boolean processInstruction() {
        try {
            Instruction inst = apicon.getInstruction();
            if(inst instanceof Goodbye) {
                return false;
            }
            InstructionResult instr = inst.execute(dbcon);
            if(instr instanceof AuthenticateResult) {
                apicon.returnResult((AuthenticateResult) instr);
                return true;
            }
            else if(instr instanceof PreRegisterResult) {
                apicon.returnResult((PreRegisterResult) instr);
                return true;
            }
            else if(instr instanceof RegisterResult) {
                apicon.returnResult((RegisterResult) instr);
                return true;

            }
            else if(instr instanceof RemoveResult) {
                apicon.returnResult((RemoveResult) instr);
                return true;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }

    static void loadConfig() {
        config = new Config();
        config.loadConfig();
        mysqlconfig = config.getMysqlConfig();
    }

}