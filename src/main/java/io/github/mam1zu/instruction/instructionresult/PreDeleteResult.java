package io.github.mam1zu.instruction.instructionresult;

public class PreDeleteResult extends InstructionResult {

    private String email;
    private String predelid;

    public PreDeleteResult(String uuid, String email, int result, String predelid) {
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
