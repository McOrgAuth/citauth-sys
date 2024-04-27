package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.InstructionResult;

public class Instruction {
    String mcid;
    Instruction (String mcid) {
        this.mcid = mcid;
    }

    public InstructionResult execute(MySQLConnection dbcon) {
        return null;
    }
}
