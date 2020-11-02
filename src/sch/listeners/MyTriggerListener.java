package sch.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTriggerListener implements TriggerListener {

    private final Logger logger = LoggerFactory.getLogger(MyTriggerListener.class);

    public String getName() {
        return MyTriggerListener.class.getName();
    }

    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        logger.info("{} trigger is fired", getName());
    }

    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        logger.info("{} was about to be executed but a TriggerListener vetoed it's execution", context.getJobDetail().getKey().toString());
        return false;
    }

    public void triggerMisfired(Trigger trigger) {
        logger.info("{} trigger was misfired", getName());
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
        logger.info("{} trigger is complete", getName());
    }
}