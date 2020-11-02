package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MariaDBManager {
    
    private final Logger LOGGER = LoggerFactory.getLogger(MariaDBManager.class);
    
    private Connection conn = null;
    
    private final String JDBC_DRIVER_MARIADB = "org.mariadb.jdbc.Driver";
    private final boolean AUTO_COMMIT = false;
    private final int VALID_TIMEOUT = 500;
    
    private String url = null;
    private String database = null;
    private String username = null;
    private String password = null;
    
    
    public MariaDBManager(){
        this.url = "jdbc:mariadb://localhost:3306";
        this.database = "dochi_dev";
        this.username = "dochi";
        this.password = "dochi";
    }
    
    public void createConnection() {
        LOGGER.info("[CREATE]");
        
        try {
            Class.forName(JDBC_DRIVER_MARIADB);
        
            Connection conn = DriverManager.getConnection(
                String.format("%s/%s", this.url, this.database),
                this.username,
                this.password
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
