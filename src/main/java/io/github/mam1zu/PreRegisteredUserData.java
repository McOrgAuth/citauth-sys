package io.github.mam1zu;
public class PreRegisteredUserData extends UserData {

    private String preregid;

    public PreRegisteredUserData(String uuid, String email, String preregid) {
        super(uuid, email);
        this.preregid = preregid;
    }

    public String getPreregid() {
        return this.preregid;
    }
}
