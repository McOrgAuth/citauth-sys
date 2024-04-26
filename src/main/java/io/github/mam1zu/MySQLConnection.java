package io.github.mam1zu;

import java.io.IOException;
import java.sql.*;


public class MySQLConnection {
    private String host;
    private String db;
    private String user;
    private String password;
    private String port;
    private boolean db_con_status = false;
    private Connection con;

    MySQLConnection(String host, String db, String user, String password, String port) {

        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
        this.db_con_status = connect();

        if(!this.db_con_status) {
            System.out.println("Couldn't establish connection to mysql");
            return;
        }
        init();
    }

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
        try {
            PreparedStatement pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS PRE_REGISTERED_USER (" +
                    "MCID varchar(40),"+
                    "AUTH_UUID varchar(40),"+
                    "REGISTERED_AT TIMESTAMP"+
                    ");");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
