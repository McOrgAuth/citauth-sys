package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.AuthenticateResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticateUser extends Instruction {
    public AuthenticateUser(String uuid) {
        super(uuid);
    }

    @Override
    public AuthenticateResult execute(MySQLConnection dbcon) {
        boolean result = false;
        result = dbcon.authenticateUser(this.uuid);
        return new AuthenticateResult(this.uuid, result);
    }
}
