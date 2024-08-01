package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RegisterResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegisterUser extends Instruction {
    String email = null;
    public RegisterUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public RegisterResult execute(MySQLConnection dbcon) {
        boolean result;
        result = dbcon.registerUser(uuid, email);
        return new RegisterResult(this.uuid, this.email, result);
    }
}
