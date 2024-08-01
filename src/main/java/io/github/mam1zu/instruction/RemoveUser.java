package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RemoveResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveUser extends Instruction {
    String email;
    public RemoveUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public RemoveResult execute(MySQLConnection dbcon) {
        boolean result;
        result = dbcon.deleteUser(uuid, email);
        return new RemoveResult(this.uuid, this.email, result);
    }
}
