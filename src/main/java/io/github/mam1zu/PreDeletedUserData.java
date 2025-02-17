package io.github.mam1zu;

public class PreDeletedUserData extends UserData{
    
    private String predelid;

    public PreDeletedUserData(String uuid, String email, String predelid) {
        super(uuid, email);
        this.predelid = predelid;
    }

    public String getPredelid() {
        return this.predelid;
    }
}
