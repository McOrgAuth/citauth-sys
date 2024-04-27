package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;

public class RegisterUser extends Instruction {
    public RegisterUser(String mcid) {
        super(mcid);
    }

    @Override
    public boolean execute(MySQLConnection dbcon) {
        return false;
    }
}
