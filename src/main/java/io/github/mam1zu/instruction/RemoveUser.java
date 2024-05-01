package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RemoveResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveUser extends Instruction {
    public RemoveUser(String mcid) {
        super(mcid);
    }

    @Override
    public RemoveResult execute(MySQLConnection dbcon) {
        PreparedStatement pstmt;
        boolean result = false;
        try {
            if(!dbcon.checkCon())
                dbcon.connect();
            pstmt = dbcon.con.prepareStatement("DELETE FROM REGISTERED_USER WHERE MCID = ?;");
            pstmt.setString(1, this.mcid);
            result = pstmt.executeUpdate() == 1;
        } catch(SQLException e) {
            e.printStackTrace();
            result = false;
        } finally {
            dbcon.disconnect();
        }
        return new RemoveResult(this.mcid, result);
    }
}
