package io.github.mam1zu.instruction.instructionresult;

public class DeleteResult extends InstructionResult {
    private String email;
    public DeleteResult(String uuid, String email, boolean result) {
        super(uuid, result);
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
