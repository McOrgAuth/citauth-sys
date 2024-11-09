package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.AuthenticateResult;

public class AuthenticateUser extends Instruction {
    public AuthenticateUser(String uuid) {
        super(uuid);
    }

    @Override
    public AuthenticateResult execute(MySQLConnection dbcon) {
        int result = 0;
        result = dbcon.authenticateUser(this.uuid);
        return new AuthenticateResult(this.uuid, result);
    }
}
