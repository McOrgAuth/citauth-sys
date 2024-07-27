package io.github.mam1zu.instruction.instructionresult;

import io.github.mam1zu.instruction.PreRegisterUser;

public class PreRegisterResult extends InstructionResult {
    private String email;
    PreRegisterResult(String uuid, String email, boolean result) {
        super(uuid, result);
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
