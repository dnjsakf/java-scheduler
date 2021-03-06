package sch.jobs.runlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.MariaDBManager;
import db.SQLiteManager;

public class RunListJob implements Job {
    
    private final Logger LOGGER = LoggerFactory.getLogger(RunListJob.class);
    
    public void testMariaDB() {
        MariaDBManager mariadb = new MariaDBManager();

        mariadb.createConnection();
        
        Connection mariaConn = mariadb.getConnection();

        try {
            PreparedStatement pstmt = mariaConn.prepareStatement("select empno, name, job  FROM dochi_dev.emp");

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Integer empno = rs.getInt(1);
                String name = rs.getString(2);
                String job = rs.getString(3);
                
                LOGGER.info(String.format("%d / %s / %s", empno, name, job));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        JobDataMap dataMap = arg0.getMergedJobDataMap();
        
        SQLiteManager sqlite = new SQLiteManager();
        
        sqlite.createConnection();
        
        Connection sqliteConn = sqlite.getConnection();
        
        try {
            PreparedStatement pstmt2 = sqliteConn.prepareStatement("select PlaylistId, Name FROM playlists");
            ResultSet rs2 = pstmt2.executeQuery();
            while(rs2.next()) {
                Integer playlistId = rs2.getInt(1);
                String name = rs2.getString(2);
                
                LOGGER.info(String.format("%d / %s", playlistId, name));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
