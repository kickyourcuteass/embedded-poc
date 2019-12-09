package ro.home.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.home.job.model.FxMarketPricesStore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
class JobResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResource.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private FxMarketPricesStore marketPricesStore;

    @Autowired
    private JobRegistry jobRegistry;

    @GetMapping(path = "/{jobName}/start/{businessDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> startJob(@PathVariable String jobName, @PathVariable String businessDate) {

        marketPricesStore.clearPriceStore();
        try {
            Job job = jobRegistry.getJob(jobName);
            JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
            return Map.of(jobName, jobName, "status", jobExecution.getStatus().name());
        } catch (NoSuchJobException e) {
            LOGGER.error(e.getMessage(), e);
            return Map.of("jobName", jobName, "status", "non-existent");
        } catch (JobExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            return Map.of("jobName", jobName, "status", "error");
        }
    }

    @GetMapping(path = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getJobNames(){
        return jobExplorer.getJobNames();
    }

    @GetMapping(path = "/{jobName}/last-job-instance", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobInstance getLastJobInstance(@PathVariable String jobName) {
        return jobExplorer.getLastJobInstance(jobName);
    }

    @GetMapping(path = "/{jobName}/unregister", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void unregisterJob(@PathVariable String jobName) {
        jobRegistry.unregister(jobName);
    }
}
