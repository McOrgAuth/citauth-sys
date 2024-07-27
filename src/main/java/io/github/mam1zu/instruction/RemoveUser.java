package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RemoveResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveUser extends Instruction {
    String email;
    public RemoveUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public RemoveResult execute(MySQLConnection dbcon) {
        PreparedStatement pstmt;
        boolean result = false;
        try {
            if(!dbcon.checkCon())
                dbcon.connect();
            pstmt = dbcon.con.prepareStatement("DELETE FROM REGISTERED_USER WHERE UUID = ? AND EMAIL = ?;");
            pstmt.setString(1, this.uuid);
            pstmt.setString(2, this.email);
            result = pstmt.executeUpdate() == 1;
        } catch(SQLException e) {
            e.printStackTrace();
            result = false;
        } finally {
            dbcon.disconnect();
        }
        return new RemoveResult(this.uuid, this.email, result);
    }
}
