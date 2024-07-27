package io.github.mam1zu.instruction.instructionresult;

public class InstructionResult {
    private String uuid;
    private boolean result;
    public InstructionResult(String uuid, boolean result) {
        this.uuid = uuid;
        this.result = result;
    }

    public String getUUID() {
        return this.uuid;
    }

    public boolean getResult() {
        return this.result;
    }
}
