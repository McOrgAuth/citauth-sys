package io.github.mam1zu.instruction.instructionresult;

public class RemoveResult extends InstructionResult {
    private String email;
    public RemoveResult(String uuid, String email, boolean result) {
        super(uuid, result);
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
