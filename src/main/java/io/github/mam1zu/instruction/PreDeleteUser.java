package io.github.mam1zu.instruction;

import java.sql.SQLException;
import java.util.UUID;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.PreDeleteResult;

public class PreDeleteUser extends Instruction {
    String email;
    public PreDeleteUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public PreDeleteResult execute(MySQLConnection dbcon) {

        int result = 0;

        String predelid = generatePredelid();

        try {

            result = dbcon.preDeleteUser(email, predelid);

        } catch (SQLException e) {

            e.printStackTrace();
            result = -100;
            dbcon.disconnect();

        }
        return new PreDeleteResult(null, this.email, result, predelid);
    }

    private String generatePredelid() {

        return UUID.randomUUID().toString();
    }
}
