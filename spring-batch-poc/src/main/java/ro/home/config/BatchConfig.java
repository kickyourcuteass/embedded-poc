package ro.home.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Import(JdbcConfig.class)
@ComponentScan(basePackages = {"ro.home.job"})
//@EnableBatchProcessing
public class BatchConfig {

    /**
     *
     * @param springBatchDataSource
     * @param springBatchJdbcTemplate
     * @param transactionManager
     * @return
     * @throws Exception
     */
    @Bean
    JobRepository jobRepository(@Qualifier("springBatchDataSource") DataSource springBatchDataSource,
                                @Qualifier("springBatchJdbcTemplate") JdbcTemplate springBatchJdbcTemplate,
                                PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean jrFactoryBean = new JobRepositoryFactoryBean();
        jrFactoryBean.setDataSource(springBatchDataSource);
        jrFactoryBean.setJdbcOperations(springBatchJdbcTemplate);
        jrFactoryBean.setTransactionManager(transactionManager);
        jrFactoryBean.setSerializer(new Jackson2ExecutionContextStringSerializer());
        jrFactoryBean.setDatabaseType("h2");
        return jrFactoryBean.getObject();
    }

    /**
     * Factory Bean designed to create jobs. (Jobs are composed of steps).
     *
     * @param jobRepository
     * @return
     */
    @Bean
    JobBuilderFactory jobBuilderFactory(JobRepository jobRepository) {
        return new JobBuilderFactory(jobRepository);
    }

    /**
     * Factory Bean designed to create steps. Steps are composed from a reader one or more processors and a writer.
     * Sequences of steps can be influenced by execution results obtained in previous steps.
     *
     * @param jobRepository
     * @param transactionManager
     * @return
     */
    @Bean
    StepBuilderFactory stepBuilderFactory(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilderFactory(jobRepository, transactionManager);
    }

    /**
     * SimpleJobLauncher
     *
     * High Level job execution interface with basic checks performed in order to launch a job execution.
     *
     * @param jobRepository
     * @param taskExecutor
     * @return
     */
    @Bean
    JobLauncher jobLauncher(JobRepository jobRepository, TaskExecutor taskExecutor) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        //jobLauncher.setTaskExecutor(taskExecutor);
        return jobLauncher;
    }

    /**
     * In memory map for storing registered / unregistered jobs. Once a job is unregistered, it can no longer be
     * executed unless the jvm instance has been restarted.
     */
    @Bean
    JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    /**
     *
     * @param jobRegistry
     * @return
     */
    @Bean
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /**
     * SimpleJobExplorer
     * Dml service interface providing a high level api for querying various information about jobExecutions.
     *
     * @param springBatchJdbcTemplate
     * @return
     * @throws Exception
     */
    @Bean
    JobExplorer jobExplorer(@Qualifier("springBatchJdbcTemplate") JdbcTemplate springBatchJdbcTemplate) throws Exception {
        JobExplorerFactoryBean jobExplorerFactory = new JobExplorerFactoryBean();
        jobExplorerFactory.setJdbcOperations(springBatchJdbcTemplate);
        jobExplorerFactory.setSerializer(new Jackson2ExecutionContextStringSerializer());
        return jobExplorerFactory.getObject();
    }

    /**
     * SimpleJobOperator
     *
     * @param jobExplorer
     * @param jobLauncher
     * @param jobRepository
     * @param jobRegistry
     * @return
     */
    @Bean
    JobOperator jobOperator(JobExplorer jobExplorer,
                            JobLauncher jobLauncher,
                            JobRepository jobRepository,
                            JobRegistry jobRegistry) {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        return jobOperator;
    }

    @Bean
    TaskExecutor taskExecutor(@Value("${task.executor.cached.capacity}") Integer cachedCapacity,
                              @Value("${task.executor.max.capacity}") Integer maxCapacity,
                              @Value("${task.executor.thread.name.prefix}") String threadPrefix) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(cachedCapacity);
        taskExecutor.setMaxPoolSize(maxCapacity);
        taskExecutor.setThreadNamePrefix(threadPrefix);
        return taskExecutor;
    }
}
