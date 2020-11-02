package sch;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sch.jobs.TestJobConfiguration;
import sch.listeners.MyScheduleListener;
import sch.listeners.MyTriggerListener;

public class ScheduleManager {
    
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleManager.class);
    
    private static StdSchedulerFactory fac = null;
    private static Scheduler sch = null;
    
    public static void run() {
        try {
            // Create Scheduler
            fac = new StdSchedulerFactory("sch/quartz.properties");
            sch = fac.getScheduler();
            
            // Set Listeners
            sch.getListenerManager().addJobListener(new MyScheduleListener());
            sch.getListenerManager().addTriggerListener(new MyTriggerListener());
            
            // Create Job
            TestJobConfiguration testJob = new TestJobConfiguration("LoggingTest", "test", "0/5 * * * * ?");
            JobDetail job = testJob.getJob();
            CronTrigger trigger = testJob.getTrigger();
            
            // Run Schedule
            sch.scheduleJob(job, trigger);
            sch.start();
            
        } catch ( ParseException | SchedulerException e ) {
            e.printStackTrace();
            try {
                if( sch != null && sch.isStarted() ) {
                    sch.shutdown();
                }
            } catch (SchedulerException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        run();
    }
}
