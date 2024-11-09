package io.github.mam1zu.instruction;

import java.sql.SQLException;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.DeleteResult;

public class DeleteUser extends Instruction {
    String email;
    public DeleteUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public DeleteResult execute(MySQLConnection dbcon) {
        int result = 0;
        try {
            result = dbcon.deleteUser(uuid, email);
        } catch (SQLException e) {
            e.printStackTrace();
            result = -100;
            dbcon.disconnect();
        }
        return new DeleteResult(this.uuid, this.email, result);
    }
}
