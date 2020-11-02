package sch.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyScheduleListener implements JobListener{
    private final Logger logger = LoggerFactory.getLogger(MyScheduleListener.class);

    public String getName() {
        return MyScheduleListener.class.getName();
    }

    public void jobToBeExecuted(JobExecutionContext context) {
        logger.info("{} is about to be executed", context.getJobDetail().getKey().toString());
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        logger.info("{} finised execution", context.getJobDetail().getKey().toString());
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        logger.info("{} was about to be executed but a JobListener vetoed it's execution", context.getJobDetail().getKey().toString());
    }
}
