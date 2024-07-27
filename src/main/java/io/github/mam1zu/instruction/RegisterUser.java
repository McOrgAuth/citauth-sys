package io.github.mam1zu.instruction;

import io.github.mam1zu.connection.MySQLConnection;
import io.github.mam1zu.instruction.instructionresult.RegisterResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegisterUser extends Instruction {
    String email = null;
    public RegisterUser(String uuid, String email) {
        super(uuid);
        this.email = email;
    }

    @Override
    public RegisterResult execute(MySQLConnection dbcon) {

        PreparedStatement checkdup_pstmt;
        PreparedStatement register_pstmt;
        ResultSet checkdup_rs;
        boolean result = false;

        try {
            if(dbcon.checkCon()) dbcon.connect();
            checkdup_pstmt = dbcon.con.prepareStatement("SELECT UUID FROM REGISTERED_USER WHERE UUID = ?;");
            checkdup_pstmt.setString(1, this.uuid);
            checkdup_rs = checkdup_pstmt.executeQuery();
            if(!checkdup_rs.next()) {
                LocalDateTime current = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dbformat_now = current.format(formatter);
                register_pstmt = dbcon.con.prepareStatement("INSERT INTO REGISTERED_USER VALUES(?, ?);");
                register_pstmt.setString(1, this.uuid);
                register_pstmt.setString(2, dbformat_now);
                result = register_pstmt.executeUpdate() == 1;
                //TODO: there must be implemented a feature to delete a pre-register information
            }
            else {
                //User this.mcid is already registered
                //do nothing at the moment
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbcon.disconnect();
        }
        return new RegisterResult(this.uuid, this.email, result);
    }
}
