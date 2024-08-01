package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RemoveResult;

public class DeleteUser extends Instruction {
    String email;
    public DeleteUser(String uuid, String email) {
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
