package sch.jobs.runlist;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.TriggerBuilder;

public class RunListJobConfiguration {
    
    private String jobName = null;
    private String jobGroup = null;
    private String cron = null;
    
    private JobDetail job = null;
    private CronTrigger trigger = null;
    
    public RunListJobConfiguration(String jobName, String jobGroup) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        
        this.job = createJob(jobName, jobGroup);
    }
    
    public RunListJobConfiguration(String jobName, String jobGroup, String cron) throws ParseException {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.cron = cron;
        
        createJob(jobName, jobGroup);
        createCrontab(jobName, jobGroup, cron);
    }
    
    private JobDetail createJob(String jobName, String jobGroup){
        job = JobBuilder.newJob(RunListJob.class)
                .withIdentity(jobName, jobGroup)
                .build();
        return job;
    }
    
    private CronTrigger createCrontab(String triggerName, String triggerGroup, String cron) throws ParseException {
        CronExpression cronExpression = new CronExpression(cron);
        CronScheduleBuilder cronSchdule = CronScheduleBuilder.cronSchedule(cronExpression);
        
        trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, triggerGroup)
                    .withSchedule(cronSchdule)
                    .forJob(this.job)
                    .build();
        
        return trigger;
    }
    

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public JobDetail getJob() {
        return job;
    }

    public void setJob(JobDetail job) {
        this.job = job;
    }

    public CronTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(CronTrigger trigger) {
        this.trigger = trigger;
    }
    
}
