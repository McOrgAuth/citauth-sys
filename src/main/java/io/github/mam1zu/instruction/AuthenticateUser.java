package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticateUser extends Instruction {
    public AuthenticateUser(String mcid) {
        super(mcid);
    }

    @Override
    public boolean execute(MySQLConnection dbcon) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        if(!dbcon.checkCon())
            dbcon.connect();
        try {
            pstmt = dbcon.con.prepareStatement("SELECT MCID FROM REGISTERED_USER WHERE MCID = ?;");
            pstmt.setString(1, this.mcid);
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return rs != null;
    }
}
