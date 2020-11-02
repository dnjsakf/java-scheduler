package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteManager {
	
    private final Logger LOGGER = LoggerFactory.getLogger(SQLiteManager.class);
    
    private final String JDBC_DRIVER_MARIADB = "org.mariadb.jdbc.Driver";
    private final boolean AUTO_COMMIT = false;
    private final int VALID_TIMEOUT = 500;

    private Connection conn = null;
    private String url = null;
    
    public SQLiteManager(){
        this.url = "jdbc:sqlite:resources/sqlite/chinook.db";
    }
    
    public void createConnection() {
        LOGGER.info("[CREATE]");
        
        try {
            Class.forName(JDBC_DRIVER_MARIADB);
        
            Connection conn = DriverManager.getConnection(
                String.format("%s", this.url)
            );
        
            conn.setAutoCommit(AUTO_COMMIT);
        
            this.conn = conn;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        LOGGER.info("[CLOSE]");
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.conn = null;
        }
    }
    
    public void ensureConnection() {
        LOGGER.info("[ENSURE]");
        try {
            if( this.conn == null || this.conn.isValid(VALID_TIMEOUT)) {
                closeConnection();
                createConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return this.conn;
    }
}
