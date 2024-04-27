package io.github.mam1zu.instruction.instructionresult;

public class InstructionResult {
    private String mcid;
    private boolean result;
    public InstructionResult(String mcid, boolean result) {
        this.mcid = mcid;
        this.result = result;
    }

    public String getMcid() {
        return this.mcid;
    }

    public boolean getResult() {
        return this.result;
    }
}
