package io.github.mam1zu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        APIConnection apicon = null;
        MySQLConnection dbcon = null;
        Scanner scan = new Scanner(System.in);

        System.out.println("CITAUTH PROCESS-SYSTEM");

        //Connecting to API
        System.out.println("Establishing connection to api server...");
        apicon = new APIConnection("172.24.241.112", 37565);
        if(!apicon.connect()) {
            System.out.println("Connection to API failed");
            System.exit(-1);
        }
        System.out.println("Connection to API established!");
        //////////////////////////////////////////////////

        //Connection to DB
        System.out.println("Establishing connection to database server...");
        dbcon = new MySQLConnection("localhost", "citauth","root", "password", "3306");
        if(!dbcon.connect()) {
            System.out.println("Connection to DB failed");
            apicon.disconnect();
            System.exit(-1);
        }
        System.out.println("Connection to DB established!");
        ///////////////////////////////////////


        apicon.disconnect();
        dbcon.disconnect();

    }
}