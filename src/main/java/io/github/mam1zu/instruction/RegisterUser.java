package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RegisterResult;

public class RegisterUser extends Instruction {
    public RegisterUser(String mcid) {
        super(mcid);
    }

    @Override
    public RegisterResult execute(MySQLConnection dbcon) {
        return null;
    }
}
