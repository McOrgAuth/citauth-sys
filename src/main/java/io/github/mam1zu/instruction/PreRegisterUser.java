package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.PreRegisterResult;

public class PreRegisterUser extends Instruction {
    String email;
    public PreRegisterUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public PreRegisterResult execute(MySQLConnection dbcon) {
        boolean result;
        result = dbcon.preRegisterUser();
        return new PreRegisterResult(uuid, email, result);
    }
}
