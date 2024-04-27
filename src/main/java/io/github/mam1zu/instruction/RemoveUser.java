package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RemoveResult;

public class RemoveUser extends Instruction {
    public RemoveUser(String mcid) {
        super(mcid);
    }

    @Override
    public RemoveResult execute(MySQLConnection dbcon) {
        return null;
    }
}
