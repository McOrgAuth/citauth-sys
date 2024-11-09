package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.PreRegisterResult;

import java.sql.SQLException;
import java.util.UUID;

public class PreRegisterUser extends Instruction {

    String email;

    public PreRegisterUser(String uuid, String email) {

        super(uuid);
        this.email = email;

    }

    @Override
    public PreRegisterResult execute(MySQLConnection dbcon) {

        int result = 0;
        String preregid = generatePreregid();

        try {

            result = dbcon.preRegisterUser(uuid, email, preregid);

        } catch (SQLException e) {

            e.printStackTrace();
            result = -100;
            dbcon.disconnect();

        }

        return new PreRegisterResult(uuid, email, result, preregid);
        
    }

    private String generatePreregid() {

        return UUID.randomUUID().toString();

    }

}
