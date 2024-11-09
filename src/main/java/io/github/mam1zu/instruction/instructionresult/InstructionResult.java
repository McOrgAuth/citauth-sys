package io.github.mam1zu.instruction.instructionresult;

public class InstructionResult {
    private String uuid;
    private int result;

    public InstructionResult(String uuid, int result) {
        this.uuid = uuid;
        this.result = result;
    }

    public String getUUID() {
        return this.uuid;
    }

    public int getResult() {
        return this.result;
    }

}
