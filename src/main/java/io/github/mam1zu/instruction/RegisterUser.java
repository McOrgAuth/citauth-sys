package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RegisterResult;

public class RegisterUser extends Instruction {
    String email = null;
    String preregid;
    public RegisterUser(String uuid, String email, String preregid) {
        super(uuid);
        this.email = email;
        this.preregid = preregid;
    }

    @Override
    public RegisterResult execute(MySQLConnection dbcon) {
        boolean result;
        result = dbcon.registerUser(uuid, email, preregid);
        return new RegisterResult(this.uuid, this.email, result);
    }
}
