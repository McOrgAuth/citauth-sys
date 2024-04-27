package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;

public class Instruction {
    String mcid;
    Instruction (String mcid) {
        this.mcid = mcid;
    }

    public boolean execute(MySQLConnection dbcon) {
        return false;
    }
}
