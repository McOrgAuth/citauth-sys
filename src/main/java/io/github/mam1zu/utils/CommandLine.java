package io.github.mam1zu.utils;

import java.util.Scanner;

public class CommandLine extends Thread{

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);
        while(true) {
            String cmd = scan.nextLine();
            if(cmd.equals("stop")) {
                //stop
            }
            else if(cmd.equals("help")) {
                //help
            }
        }
    }
}
