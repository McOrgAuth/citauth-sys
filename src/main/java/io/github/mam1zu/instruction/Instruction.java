package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.InstructionResult;

public class Instruction {
    String uuid;
    Instruction (String uuid) {
        this.uuid = uuid;
    }

    public InstructionResult execute(MySQLConnection dbcon) {
        return null;
    }
}
