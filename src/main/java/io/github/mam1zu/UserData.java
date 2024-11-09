package io.github.mam1zu;
public class UserData {
    
    private String uuid;
    private String email;

    UserData(String uuid, String email) {
        this.email = email;
        this.uuid = uuid;
    }

    public String getUUID() {
        return this.uuid;
    }

    public String getEmail() {
        return this.email;
    }
    
}
