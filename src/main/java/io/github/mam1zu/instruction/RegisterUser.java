package io.github.mam1zu.instruction;

import java.sql.SQLException;

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

        int result = 0;

        try {

            result = dbcon.registerUser(uuid, email, preregid);

        } catch (SQLException e) {

            e.printStackTrace();
            result = -100;
            dbcon.disconnect();

        }

        return new RegisterResult(this.uuid, this.email, preregid, result);

    }
    
}
