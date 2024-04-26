package io.github.mam1zu;

public class Main {

    public static void main(String[] args) {

        System.out.println("CITAUTH PROCESS-SYSTEM");
        System.out.println("Establishing connection to api server...");

        //Connecting to API
        APIConnection apicon = new APIConnection("172.24.241.112", 37565);
        if(!apicon.connect()) {
            System.out.println("Connection failed");
            System.exit(-1);
        }

        System.out.println("Connection established!");
        apicon.disconnect();
        System.out.println("");



    }
}