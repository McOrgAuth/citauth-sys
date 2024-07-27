package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.PreRegisterResult;

public class PreRegisterUser extends Instruction {
    public PreRegisterUser(String uuid) {
        super(uuid);
    }

    @Override
    public PreRegisterResult execute(MySQLConnection dbcon) {
        return null;
    }
}
