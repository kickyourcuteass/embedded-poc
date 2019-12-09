package ro.home.job.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
class StepCompletionListener implements StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepCompletionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        LOGGER.info("Before execution of step {} of job {} ", stepExecution.getStepName(), jobName);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        LOGGER.info("Step {} of job {} has been executed with status {}",
                stepExecution.getStepName(), jobName, stepExecution.getExitStatus().getExitCode());
        return stepExecution.getExitStatus();
    }
}
