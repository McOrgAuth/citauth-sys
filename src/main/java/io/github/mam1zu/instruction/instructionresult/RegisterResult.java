package io.github.mam1zu.instruction.instructionresult;

public class RegisterResult extends InstructionResult {

    private String email;
    private String preregid;

    public RegisterResult(String uuid, String email, String preregid, int result) {
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
