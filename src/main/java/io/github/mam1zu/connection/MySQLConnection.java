package io.github.mam1zu.connection;

import io.github.mam1zu.connection.AccessConnection;

import java.sql.*;


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
            PreparedStatement pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS PRE_REGISTERED_USER (" +
                    "MCID varchar(40),"+
                    "AUTH_UUID varchar(40),"+
                    "PRE_REGISTERED_AT TIMESTAMP"+
                    ");");
            pstmt.execute();
            pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS REGISTERED_USER (" +
                    "MCID varchar(40),"+
                    "REGISTERED_AT TIMESTAMP"+
                    ");");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }

    }

    public boolean checkCon() {
        return this.con != null;
    }

    public boolean userAuth(String mcid) {
        boolean ret = false;
        try {
            if(this.con.isClosed()) {
                this.connect();
            }
            PreparedStatement pstmt = this.con.prepareStatement("SELECT MCID FROM REGISTERED_USER WHERE ID = ?");
            pstmt.setString(1, mcid);//To prevent from SQL-Injection
            ResultSet rs = pstmt.executeQuery();
            ret = rs.getString(1) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return ret;
    }

    public final boolean registerUser() {
        return false;
    }

    public final boolean preRegisterUser() {
        return false;
    }

    public final boolean removeUser() {
        return false;
    }

}
