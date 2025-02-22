package io.github.mam1zu.instruction.instructionresult;

public class DeleteResult extends InstructionResult {
    private String email;
    private String predelid;
    public DeleteResult(String uuid, String email, String predelid, int result) {
        super(uuid, result);
        this.email = email;
        this.predelid = predelid;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPredelid() {
        return this.predelid;
    }
}
