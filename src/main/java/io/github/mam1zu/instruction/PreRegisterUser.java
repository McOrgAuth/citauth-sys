package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.PreRegisterResult;

import java.util.UUID;

public class PreRegisterUser extends Instruction {
    String email;
    public PreRegisterUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public PreRegisterResult execute(MySQLConnection dbcon) {
        boolean result;
        String preregid = generatePreregid();
        result = dbcon.preRegisterUser(uuid, email, preregid);
        return new PreRegisterResult(uuid, email, result, preregid);
    }

    private String generatePreregid() {
        return UUID.randomUUID().toString();
    }
}
