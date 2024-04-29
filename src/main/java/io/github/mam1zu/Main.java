package io.github.mam1zu;

import io.github.mam1zu.connection.APIConnection;
import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.AuthenticateUser;
import io.github.mam1zu.instruction.Instruction;
import io.github.mam1zu.instruction.instructionresult.*;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static APIConnection apicon = null;
    static MySQLConnection dbcon = null;
    public static void main(String[] args) {

        final Scanner scan = new Scanner(System.in);
        System.out.println("CITAUTH PROCESS-SYSTEM");

        establishAPIConnection();
        checkMySQLConnection();

        while(true) {
            processInstruction();
            break;
        }



        apicon.disconnect();
        dbcon.disconnect();

    }

    static void establishAPIConnection() {
        System.out.println("Establishing connection to api server...");
        apicon = new APIConnection("172.24.241.112", 37565);
        if(!apicon.connect()) {
            System.out.println("Connection to API failed");
            System.exit(-1);
        }
        System.out.println("Connection to API established!");
    }

    static void checkMySQLConnection() {
        System.out.println("Checking connection to database server...");
        dbcon = new MySQLConnection("localhost", "citauth","root", "kouki1230", "3306");
        if(!dbcon.connect()) {
            System.out.println("Connection to DB failed");
            apicon.disconnect();
            System.exit(-1);
        }
        System.out.println("Connection to DB successed!");
    }

    static boolean processInstruction() {
        try {
            Instruction inst = apicon.getInstruction();
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
        return false;
    }

}