package io.github.mam1zu.instruction.instructionresult;

import io.github.mam1zu.instruction.PreRegisterUser;

public class PreRegisterResult extends InstructionResult {
    private String email;
    private String preregid;
    public PreRegisterResult(String uuid, String email, int result, String preregid) {
        super(uuid, result);
        this.email = email;
        this.preregid = preregid;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPreregid() {
        return this.preregid;
    }
}
