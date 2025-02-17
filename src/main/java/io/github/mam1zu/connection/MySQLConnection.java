package io.github.mam1zu.connection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.mam1zu.PreDeletedUserData;
import io.github.mam1zu.PreRegisteredUserData;
import io.github.mam1zu.RegisteredUserData;

/**
 * データベース操作処理を実装するためのクラス
 * @author mamizu
 */


public final class MySQLConnection extends AccessConnection {
    private String host;
    private String db;
    private String user;
    private String password;
    private String port;
    public Connection con;

    /**
     * MySQLへのコネクションを作成します。
     * @param host MySQLデータベースのホスト名。
     * @param db MySQLデータベースのデータベース名。
     * @param user MySQLデータベースに接続する際に使用するユーザ名。
     * @param password MySQLデータベースに接続する際に使用するパスワード。
     * @param port MySQLデータベースが待ち受けているポート番号。
     */

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


    /**
     * MySQLデータベースへ接続します。
     * @return データベースへのアクセス状況。
     */
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
    
    /**
     * MySQLデータベースから切断します。
     */

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

    /**
     * データベースを初期化処理を行います。
     * テーブルが存在しなければ作成し、存在する場合は何もしません。
     */

    public void init() {
        this.connect();
        try {
            PreparedStatement pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS PREREGISTERED_USER (" +
                    "EMAIL TEXT NOT NULL,"+
                    "UUID TEXT NOT NULL,"+
                    "PREREG_ID TEXT NOT NULL,"+
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
            pstmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS PREDELETED_USER (" +
                    "EMAIL TEXT NOT NULL,"+
                    "UUID TEXT NOT NULL,"+
                    "PREDEL_ID TEXT NOT NULL,"+
                    "created_at TIMESTAMP NOT NULL,"+
                    "expire_at TIMESTAMP NOT NULL"+
                    ");");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }

    }
    
    /**
     * データベースへのコネクションの状況を返します。
     * @return データベースへのコネクションの状況。
     */

    public boolean checkCon() {
        try {
            return !this.con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ユーザがシステムに登録されているかどうかを確認します。
     * @param uuid 認証したいユーザのuuid。
     * @return 認証結果。認証が成功すれば1、失敗すれば-1。
     */

    public int authenticateUser(String uuid) {
        int ret = -1;
        try {
            if(this.con.isClosed()) {
                this.connect();
            }
            PreparedStatement pstmt = this.con.prepareStatement("SELECT UUID FROM REGISTERED_USER WHERE UUID = ?");
            pstmt.setString(1, uuid);//To prevent from SQL-Injection
            ResultSet rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                ret = -1;
            }
            else {
                ret = 1;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return ret;
    }

    /**
     * ユーザの削除申請をデータベースに登録します。
     * @param uuid 削除申請を行うユーザのuuid。
     * @param email 削除申請を行うユーザのメールアドレス。
     * @param predelid 削除申請のID。
     * @return 削除申請登録結果。1:成功。-1:失敗。ユーザが存在しない。-100:例外エラーが発生した場合はこの値を返すこと。
     */

    public int preDeleteUser(String email, String predelid) throws SQLException {

        int ret = 0;

        if(this.con.isClosed()) {
            this.connect();
        }

        if(!isRegistered(email)) {
            //ユーザが存在しない
            return -1;
        }

        if(isPreDeleted(email)) {
            //既に削除申請情報が存在するため、先に消す
            deletePredeletedData(email);
        }

        RegisteredUserData user = getRegisteredUser(email);
        
        String uuid = user.getUUID();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(15);//expire time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now_formatted = now.format(formatter);
        String expire_formatted = expire.format(formatter);

        PreparedStatement pstmt = this.con.prepareStatement("INSERT INTO PREDELETED_USER VALUES(?, ?, ?, ?, ?)");
        pstmt.setString(1, email);
        pstmt.setString(2, uuid);
        pstmt.setString(3, predelid);
        pstmt.setString(4, now_formatted);
        pstmt.setString(5, expire_formatted);

        if(pstmt.executeUpdate() != 1) {
            //失敗
            System.err.println("registration of predelete data failed.");
            return -1;
        }

        System.out.println("PRDL: "+email+", PREDEL_ID:"+predelid);
        ret = 1;
        
        return ret;

    }

    public int deleteUser(String predelid) throws SQLException {

        int ret = 0;
        String uuid;
        String email;

        if(this.con.isClosed()) {
            this.connect();
        }

        PreDeletedUserData pre_user = getPredeletedUser(predelid);
        
        if(pre_user == null) {
            return -2;
        }

        if(!isPredeleteValid(predelid)) {
            //期限切れの削除申請情報発見、君消す
            deletePredeletedData(predelid);
            return -3;
        }

        uuid = pre_user.getUUID();
        email = pre_user.getEmail();

        PreparedStatement pstmt_delete = this.con.prepareStatement("DELETE FROM REGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
        pstmt_delete.setString(1, uuid);
        pstmt_delete.setString(2, email);

        if(pstmt_delete.executeUpdate() != 1) {
            //失敗
            return -1;
        }

        deletePredeletedData(predelid);

        ret = 1;

        this.disconnect();

        return ret;

    }
    
    /**
     * 仮登録されたユーザの本登録を行います。
     * @param uuid 本登録するユーザのuuidです。
     * @param email 本登録するユーザーのメールアドレスです。
     * @param preregid 仮登録の際に発行された仮登録IDです。
     * @return 本登録結果。1:成功。-1:失敗。すでに本登録されている。-2:失敗。仮登録情報が存在しなかった。-3:失敗。仮登録の期限切れ。-100:失敗。例外エラーが発生した場合はこの値を返すこと。
     * 
     */

    public int registerUser(String uuid, String email, String preregid) throws SQLException {

        int ret = 0;
        
        if(this.con.isClosed()) {
            this.connect();
        }

        PreRegisteredUserData pre_user = getPreregisteredUser(preregid);

        if(pre_user == null) {
            return -2;
        }
        
        uuid = pre_user.getUUID();
        email = pre_user.getEmail();

        if(isRegistered(uuid, email)) {
            return -1;
        }

        if(!isPreRegistered(uuid, email)) {
            return -2;
        }

        if(!isPreRegisterValid(preregid)) {
            return -3;
        }

        System.out.println(uuid + ":" + email);

        PreparedStatement pstmt_register = this.con.prepareStatement("INSERT INTO REGISTERED_USER VALUES (?, ?, ?, ?)");
        pstmt_register.setString(1, email);
        pstmt_register.setString(2, uuid);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now_formatted = now.format(formatter);
        pstmt_register.setString(3, now_formatted);
        pstmt_register.setString(4, now_formatted);

         //delete preregistration information if registration succeeded

        if(pstmt_register.executeUpdate() == 1) {
            PreparedStatement pstmt_delpre = this.con.prepareStatement("DELETE FROM PREREGISTERED_USER WHERE EMAIL = ? AND UUID = ?");
            pstmt_delpre.setString(1, email);
            pstmt_delpre.setString(2, uuid);
            if(pstmt_delpre.executeUpdate() != 1) {
                System.err.println("A registration succeeded, but deleting pre-register information failed.");
                System.err.println("UUID:"+uuid+", email:"+email);
            }
            ret = 1;
        }
        
        this.disconnect();
        return ret;

    }

    /**
     * ユーザの仮登録を行います。
     * @param uuid 登録するユーザーのuuidです。
     * @param email 登録するユーザーのメールアドレスです。
     * @param preregid 仮登録IDです。
     * @return 1: 成功。-1:この値は返さない。-2:失敗。仮登録に失敗した。-3:失敗。すでに本登録されている。-100:失敗。例外エラーにより失敗した場合はこの値を返すこと。
     * @throws SQLException
     */

    public int preRegisterUser(String uuid, String email, String preregid) throws SQLException {
        int ret = 0;
        if(this.con.isClosed()) {
            this.connect();
        }

        //register dup check... doesn't delete it here, delete registration data if you'd like to re-register
        if(isRegistered(email)) {
            return -3;
        }

        //preregister dup check... delete it here.
        if(isPreRegistered(uuid, email)) {
            deletePreregistrationData(uuid, email);
        }
        

        //preregister dup check and delete older preregistration if exists
        PreparedStatement pstmt_chkdup = this.con.prepareStatement("SELECT * FROM PREREGISTERED_USER WHERE EMAIL = ?");
        ResultSet rs;
        pstmt_chkdup.setString(1, email);
        rs = pstmt_chkdup.executeQuery();

        if(rs.isBeforeFirst()) {
            rs.close();
            deletePreregistrationData(uuid, email);
        }

        PreparedStatement pstmt_prereg = this.con.prepareStatement("INSERT INTO PREREGISTERED_USER VALUES(?, ?, ?, ?, ?)");

        pstmt_prereg.setString(1, email);
        pstmt_prereg.setString(2, uuid);
        pstmt_prereg.setString(3, preregid);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(15);//expire time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now_formatted = now.format(formatter);
        String expire_formatted = expire.format(formatter);
        pstmt_prereg.setString(4, now_formatted);
        pstmt_prereg.setString(5, expire_formatted);
        if(pstmt_prereg.executeUpdate() == 1)
            ret = 1;
        else
            ret = -2;
        this.disconnect();
        return ret;
    }

    public int deleteUser(String uuid, String email) throws SQLException {
        int ret = 0;
        if(this.con.isClosed()) {
            this.connect();
        }

        if(!isRegistered(uuid, email)) {
            return -1;
        }

        PreparedStatement pstmt = this.con.prepareStatement("DELETE FROM REGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
        pstmt.setString(1, uuid);
        pstmt.setString(2, email);
        ret = pstmt.executeUpdate() == 1 ? 1 : -1;

        this.disconnect();
        return ret;

    }



    /**
     * 指定されたユーザの仮登録情報を削除します。
     * @param uuid ユーザのuuid。
     * @param email ユーザのメールアドレス。
     * @return boolean。削除に成功すればtrue、失敗すればfalse。
     * @throws SQLException
     */
    private boolean deletePreregistrationData(String uuid, String email) throws SQLException {

        PreparedStatement pstmt = this.con.prepareStatement("DELETE FROM PREREGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
        pstmt.setString(1, uuid);
        pstmt.setString(2, email);

        return pstmt.executeUpdate() >= 1;
    }
    /**
     * 指定されたユーザがすでに登録されているかどうかを確認します。
     * @param uuid ユーザのuuid。
     * @param email_hash ユーザのメールアドレスのハッシュ。
     * @return boolean。すでに存在すればtrue、存在しなければfalse。
     * @throws SQLException
     */
    private boolean isRegistered(String uuid, String email) throws SQLException {
        PreparedStatement pstmt_dupcheck = this.con.prepareStatement("SELECT UUID, EMAIL FROM REGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
        ResultSet rs_dupcheck;
        pstmt_dupcheck.setString(1, uuid);
        pstmt_dupcheck.setString(2, email);
        rs_dupcheck = pstmt_dupcheck.executeQuery();
        return rs_dupcheck.isBeforeFirst();
    }

    private boolean isRegistered(String email) throws SQLException {
        PreparedStatement pstmt_dupcheck = this.con.prepareStatement("SELECT EMAIL FROM REGISTERED_USER WHERE EMAIL = ?");
        ResultSet rs_dupcheck;
        pstmt_dupcheck.setString(1, email);
        rs_dupcheck = pstmt_dupcheck.executeQuery();
        return rs_dupcheck.isBeforeFirst();
    }

    /**
     * 指定されたユーザがすでに仮登録されているかどうかを確認します。
     * @param uuid ユーザのuuid。
     * @param email ユーザのメールアドレス。
     * @return boolean。すでに存在すればtrue、存在しなければfalse。
     * @throws SQLException
     */
    private boolean isPreRegistered(String uuid, String email) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement("SELECT UUID, EMAIL FROM PREREGISTERED_USER WHERE UUID = ? AND EMAIL = ?");
        pstmt.setString(1, uuid);
        pstmt.setString(2, email);
        return pstmt.executeQuery().isBeforeFirst();
    }

    /**
     * 指定されたpreregidの仮登録情報を返します。
     * @param preregid 仮登録id。
     * @return PreRegisteredUser インスタンス。存在しない場合はnull。
     * @throws SQLException
     */
    private PreRegisteredUserData getPreregisteredUser(String preregid) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement("SELECT UUID, EMAIL FROM PREREGISTERED_USER WHERE PREREG_ID = ?");
        ResultSet rs;
        pstmt.setString(1, preregid);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return new PreRegisteredUserData(rs.getString("UUID"), rs.getString("EMAIL"), preregid);
        }
        else {
            return null;
        }
    }

    /**
     * 指定されたpredelidの削除申請情報を返します。
     * @param preregid 削除申請id。
     * @return PreDeletedUser インスタンス。存在しない場合はnull。
     * @throws SQLException
     */

     private PreDeletedUserData getPredeletedUser(String predelid) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement("SELECT UUID, EMAIL FROM PREDELETED_USER WHERE PREDEL_ID = ?");
        ResultSet rs;
        pstmt.setString(1, predelid);
        rs = pstmt.executeQuery();
        if(rs.next())
            return new PreDeletedUserData(rs.getString("UUID"), rs.getString("EMAIL"), predelid);
        else
            return null;
     }

    /**
     * 指定されたpreregidの仮登録情報が有効であるかどうかを確認します。
     * @param preregid
     * @return boolean。仮登録情報は有効である場合はtrue。期限切れ、または存在しない場合はfalse。
     * @throws SQLException
     */

    private boolean isPreRegisterValid(String preregid) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement("SELECT EXPIRE_AT FROM PREREGISTERED_USER WHERE PREREG_ID = ?");
        ResultSet rs;
        pstmt.setString(1, preregid);
        rs = pstmt.executeQuery();
        if(!rs.next()) {
            return false;
        }
        else {
            Timestamp expire_at = rs.getTimestamp("EXPIRE_AT");
            Timestamp now =  new Timestamp(System.currentTimeMillis());
            return !now.after(expire_at);
        }
    }

    /**
     * 引数のメールアドレスからハッシュを生成します．
     * @param email，メールアドレス
     * @return String メールアドレスから生成したハッシュ．
     */

    private String generateEmailHash(String email) {

        MessageDigest sha3_256 = null;

        try {
            sha3_256 = MessageDigest.getInstance("SHA3_256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("そんなアルゴねえよ 知らねえよ 黙れよそんなアルゴねえよ 平文こそが正義 アルゴなんかねえよ 正しいのは平文");
            e.printStackTrace();
        }

        byte[] sha3_result = sha3_256.digest(email.getBytes());
        return sha3_result.toString();

    }

    /**
     * 指定されたユーザの削除申請が既に登録されているかどうかを確認します。
     * @param email メールアドレス。
     * @return boolean。既に削除申請されている場合はtrue、されていない場合はfalse。
     * @throws SQLException
     */

    private boolean isPreDeleted(String email) throws SQLException {

        PreparedStatement pstmt = this.con.prepareStatement("SELECT EMAIL FROM PREDELETED_USER WHERE EMAIL = ?");
        pstmt.setString(1, email);
        return pstmt.executeQuery().isBeforeFirst();

    }

    private boolean deletePredeletedData(String predelid) throws SQLException {

        PreparedStatement pstmt = this.con.prepareStatement("DELETE FROM PREDELETED_USER WHERE PREDEL_ID = ?");
        pstmt.setString(1, predelid);
        return pstmt.executeUpdate() == 1;

    }

    private boolean isPredeleteValid(String predelid) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement("SELECT EXPIRE_AT FROM PREDELETED_USER WHERE PREDEL_ID = ?");
        ResultSet rs;
        pstmt.setString(1, predelid);
        rs = pstmt.executeQuery();
        if(!rs.next()) {
            return false;
        }
        else {
            Timestamp expire_at = rs.getTimestamp("EXPIRE_AT");
            Timestamp now =  new Timestamp(System.currentTimeMillis());
            return !now.after(expire_at);
        }
    }

    private RegisteredUserData getRegisteredUser(String email) throws SQLException {
        PreparedStatement pstmt = this.con.prepareStatement("SELECT EMAIL, UUID FROM REGISTERED_USER WHERE EMAIL = ?");
        ResultSet rs;
        pstmt.setString(1, email);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return new RegisteredUserData(rs.getString("UUID"), rs.getString("EMAIL"));
        }
        else {
            return null;
        }
    }




}
