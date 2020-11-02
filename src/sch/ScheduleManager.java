package sch;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sch.jobs.runlist.RunListJobConfiguration;
import sch.listeners.MyScheduleListener;
import sch.listeners.MyTriggerListener;

public class ScheduleManager extends Thread {
    
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleManager.class);
    
    private static StdSchedulerFactory fac = null;
    private static Scheduler sch = null;
    
    public ScheduleManager(){
        try {
            // Create Scheduler
            fac = new StdSchedulerFactory("resources/quartz/quartz.properties");
            sch = fac.getScheduler();
            
            // Set Listeners
            sch.getListenerManager().addJobListener(new MyScheduleListener());
            sch.getListenerManager().addTriggerListener(new MyTriggerListener());
            
        } catch ( SchedulerException e ) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        try {
            // Create Job
            RunListJobConfiguration testJob = new RunListJobConfiguration("LoggingTest", "test", "0/5 * * * * ?");
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
}
