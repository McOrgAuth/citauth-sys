package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;

public class RemoveUser extends Instruction {
    public RemoveUser(String mcid) {
        super(mcid);
    }

    @Override
    public boolean execute(MySQLConnection dbcon) {
        return false;
    }
}
