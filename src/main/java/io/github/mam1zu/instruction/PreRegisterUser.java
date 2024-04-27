package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;

public class PreRegisterUser extends Instruction {
    public PreRegisterUser(String mcid) {
        super(mcid);
    }

    @Override
    public boolean execute(MySQLConnection dbcon) {
        return false;
    }
}
