package io.github.mam1zu.connection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public final class MySQLConnection extends AccessConnection {
    private String host;
    private String db;
    private String user;
    private String password;
    private String port;
    public Connection con;

    public MySQLConnection(String host, String db, String user, String password, String port) {

        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
        connect();
        if(!this.checkCon()) {
            System.out.println("Couldn't establish connection to mysql");
            return;
        }
        disconnect();
        init();
    }

    @Override
    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.port+"/"+this.db, this.user, this.password);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
        return this.con != null;
    }

    @Override
    public void disconnect() {
        if(this.con != null) {
            try {
                this.con.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void init() {
        this.connect();
        try {
            PreparedStatement pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS PREREGISTERED_TABLE (" +
                    "EMAIL TEXT NOT NULL,"+
                    "UUID TEXT NOT NULL,"+
                    "registered_at TIMESTAMP NOT NULL,"+
                    "expire_at TIMESTAMP NOT NULL"+
                    ");");
            pstmt.execute();
            pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS REGISTERED_USER (" +
                    "EMAIL TEXT NOT NULL,"+
                    "UUID TEXT NOT NULL,"+
                    "registered_at TIMESTAMP NOT NULL,"+
                    "updated_at TIMESTAMP NOT NULL"+
                    ");");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }

    }

    public boolean checkCon() {
        try {
            return !this.con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticateUser(String uuid) {
        boolean ret = false;
        try {
            if(this.con.isClosed()) {
                this.connect();
            }
            PreparedStatement pstmt = this.con.prepareStatement("SELECT UUID FROM REGISTERED_USER WHERE UUID = ?");
            pstmt.setString(1, uuid);//To prevent from SQL-Injection
            ResultSet rs = pstmt.executeQuery();
            ret = rs.getString(1) != null;
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return ret;
    }

    public boolean registerUser(String uuid, String email) {
        boolean ret = false;
        try {
            if(this.con.isClosed()) {
                this.connect();
            }

            if(checkExistance(uuid, email)) {
                return false;
            }

            //check pre-register information
            PreparedStatement pstmt_chkpre = this.con.prepareStatement("SELECT UUID, EMAIL FROM PREREGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
            ResultSet rs;
            pstmt_chkpre.setString(1, uuid);
            pstmt_chkpre.setString(2, email);
            rs = pstmt_chkpre.executeQuery();
            if(!rs.getString(1).equalsIgnoreCase(uuid) || !rs.getString(2).equalsIgnoreCase(email)) {
                //pre-register information not found
                return false;
            }
            rs.close();


            //register
            PreparedStatement pstmt_register = this.con.prepareStatement("INSERT INTO REGISTERED_USER VALUES (?, ?, ?, ?)");
            pstmt_register.setString(1, uuid);
            pstmt_register.setString(2, email);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now_formatted = now.format(formatter);
            pstmt_register.setString(3, now_formatted);
            pstmt_register.setString(4, now_formatted);
            ret = pstmt_register.executeUpdate() == 1;

            //delete preregistration information if registration succeeded
            if(ret) {
                PreparedStatement pstmt_delpre = this.con.prepareStatement("DELETE FROM PREREGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
                pstmt_delpre.setString(1, uuid);
                pstmt_delpre.setString(2, email);
                if(pstmt_delpre.executeUpdate() != 1) {
                    System.err.println("A registration succeeded, but deleting pre-register information failed.");
                    System.err.println("UUID:"+uuid+", email:"+email);
                }
            }

            return ret;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return false;
    }

    public boolean preRegisterUser(String uuid, String email) {
        boolean ret = false;

        try {
            if(this.con.isClosed()) {
                this.connect();
            }

            //preregister dup check
            PreparedStatement pstmt_chkdup = this.con.prepareStatement("SELECT UUID, EMAIL FROM PREREGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
            ResultSet rs;
            pstmt_chkdup.setString(1, uuid);
            pstmt_chkdup.setString(2, email);
            rs = pstmt_chkdup.executeQuery();
            if(rs.getString(1).equalsIgnoreCase(uuid) || rs.getString(2).equalsIgnoreCase(email)) {
                rs.close();
                return false;
            }

            PreparedStatement pstmt_prereg = this.con.prepareStatement("INSERT INTO PREREGISTERED_USER VALUES(?, ?, ?, ?)");
            pstmt_prereg.setString(1, uuid);
            pstmt_prereg.setString(2, email);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expire = now.plusMinutes(30);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now_formatted = now.format(formatter);
            String expire_formatted = expire.format(formatter);
            pstmt_prereg.setString(3, now_formatted);
            pstmt_prereg.setString(4, expire_formatted);
            ret = pstmt_prereg.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return ret;
    }

    public boolean deleteUser(String uuid, String email) {
        boolean ret = false;
        try {
            if(this.con.isClosed()) {
                this.connect();
            }

            if(!checkExistance(uuid, email)) {
                return false;
            }

            PreparedStatement pstmt = this.con.prepareStatement("DELETE FROM REGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
            pstmt.setString(1, uuid);
            pstmt.setString(2, email);
            ret = pstmt.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }

        return ret;

    }

    public boolean checkExistance(String uuid, String email) throws SQLException {
        PreparedStatement pstmt_dupcheck = this.con.prepareStatement("SELECT UUID, EMAIL FROM REGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
        ResultSet rs_dupcheck;
        pstmt_dupcheck.setString(1, uuid);
        pstmt_dupcheck.setString(2, email);
        rs_dupcheck = pstmt_dupcheck.executeQuery();
        return rs_dupcheck.getString(1).equalsIgnoreCase(uuid) || rs_dupcheck.getString(2).equalsIgnoreCase(email);
    }

}
