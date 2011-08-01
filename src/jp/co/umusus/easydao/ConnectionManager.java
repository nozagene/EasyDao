/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.umusus.easydao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
/**
 *
 * @author nozaki
 */
public class ConnectionManager {

    /** DB ドライバ*/
    private static String dbDriver;
    /** DB 接続先URL*/
    private static String dbUrl;
    /** DB ユーザーID*/
    private static String dbUserid;
    /** DB パスワード*/
    private static String dbPassword;

    private static final Logger logger__ = Logger.getLogger(ConnectionManager.class);

    /**
     * @return the dbDriver
     */
    public static String getDbDriver() {
        return dbDriver;
    }

    /**
     * @param aDbDriver the dbDriver to set
     */
    public static void setDbDriver(String aDbDriver) {
        dbDriver = aDbDriver;
    }

    /**
     * @return the dbUrl
     */
    public static String getDbUrl() {
        return dbUrl;
    }

    /**
     * @param aDbUrl the dbUrl to set
     */
    public static void setDbUrl(String aDbUrl) {
        dbUrl = aDbUrl;
    }

    /**
     * @return the dbUserid
     */
    public static String getDbUserid() {
        return dbUserid;
    }

    /**
     * @param aDbUserid the dbUserid to set
     */
    public static void setDbUserid(String aDbUserid) {
        dbUserid = aDbUserid;
    }

    /**
     * @return the dbPassword
     */
    public static String getDbPassword() {
        return dbPassword;
    }

    /**
     * @param aDbPassword the dbPassword to set
     */
    public static void setDbPassword(String aDbPassword) {
        dbPassword = aDbPassword;
    }

    private Connection connection = null;

    public ConnectionManager(){
        init();
    }
    public ConnectionManager(String dbDriver,String dbUrl,String dbUserid,String dbPassword){
        setDbDriver(dbDriver);
        setDbUrl(dbUrl);
        setDbUserid(dbUserid);
        setDbPassword(dbPassword);
        init();
    }


    //    データベースの接続設定のメソッドを実装しておく
    private void init(){
        try {
            // コネクションを取得
            Class.forName(getDbDriver());
        } catch (ClassNotFoundException ex) {
            logger__.error("ClassNotFound", ex);
        }
        try {
            connection = DriverManager.getConnection(getDbUrl(), getDbUserid(), getDbPassword());
            connection.setAutoCommit(false); //auto commitをOFFにする
        } catch (SQLException ex) {
            logger__.error("SQLException", ex);
        }
    }

    /**
    * コネクションを返す
    */
    public java.sql.Connection getConnection() {
        return connection;
    }

    /**
     * コネクションをコミットする
     */
    public void commit() {
        DbUtils.commitAndCloseQuietly( connection );
    }
    /**
     * コネクションをコミットする
     */
    public void rollback() {
        try {
            DbUtils.rollback(connection);    //ロールバック
            DbUtils.closeQuietly(connection);
        } catch (SQLException ex){
            logger__.error("SQLException", ex);
        }
        cleanUp();
    }
    /**
     * コネクションを切断する
     */
    public void cleanUp() {
        try {
            DbUtils.close( connection );
        } catch ( Exception ex ) {
            logger__.error("SQLException", ex);
        } finally {
            if ( connection != null ) {
                connection = null;
            }
        }
    }
}