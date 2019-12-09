package ro.home.config;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ro.home.job.model.FxMarketEvent;
import ro.home.job.model.FxMarketPricesStore;
import ro.home.job.model.StockPriceDetails;
import ro.home.job.model.Trade;
import ro.home.job.step.processor.FxMarketEventProcessor;
import ro.home.job.step.reader.FxMarketEventReader;
import ro.home.job.step.reader.StockPriceDetailsReader;
import ro.home.job.step.writer.StockPriceAggregator;
import ro.home.job.step.tasklet.PriceStoreTasklet;
import ro.home.job.step.writer.StockPriceDetailsJdbcWriter;

@Configuration
@Import(BatchConfig.class)
class JobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private JobExecutionListener jobExecutionListener;

    @Bean
    public Job testJob(FxMarketPricesStore fxMarketPricesStore,
                       JdbcBatchItemWriter<StockPriceDetails> stockPriceDetailsWriter,
                       ChunkListener chunkListener,
                       StepExecutionListener stepExecutionListener,
                       TaskExecutor taskExecutor,
                       @Value("${test.job.step1.batch.size}") Integer step1BatchSize,
                       @Value("${test.job.step1.maximum.execution.threads}") Integer step1Threads,
                       @Value("${test.job.step3.batch.size}") Integer step3BatchSize,
                       @Value("${test.job.step3.maximum.execution.threads}") Integer step3Threads) {
        return jobBuilderFactory.get("testJob")
                .listener(jobExecutionListener)
                //.preventRestart()
                .start(stepBuilderFactory.get("Extract/Transform/Aggregate/Load Market Store Data")
                        // incrementer(new RunIdIncrementer())
                        .<FxMarketEvent, Trade>chunk(step1BatchSize)
                        .reader(new FxMarketEventReader("input/trades.csv"))
                        .processor(new FxMarketEventProcessor())
                        .writer(new StockPriceAggregator(fxMarketPricesStore))
                        .listener(chunkListener)
                        .listener(stepExecutionListener)
                        .taskExecutor(taskExecutor)
                        .throttleLimit(step1Threads)
                        .allowStartIfComplete(true)
                        .build()
                ).next(stepBuilderFactory.get("Display Market Stock Metadata")
                        .tasklet(new PriceStoreTasklet(fxMarketPricesStore))
                        .listener(chunkListener)
                        .listener(stepExecutionListener)
                        .allowStartIfComplete(true)
                        .build()
                ).next(stepBuilderFactory.get("Persist Market Store Data")
                        .<StockPriceDetails, StockPriceDetails>chunk(step3BatchSize)
                        .reader(new StockPriceDetailsReader(fxMarketPricesStore.getStockPriceEntries()))
                        .processor(new PassThroughItemProcessor<>())
                        .writer(stockPriceDetailsWriter)
                        .listener(chunkListener)
                        .listener(stepExecutionListener)
                        .taskExecutor(taskExecutor)
                        .throttleLimit(step3Threads)
                        .allowStartIfComplete(true)
                        .build()
                ).build();
    }

    @Bean
    JdbcBatchItemWriter<StockPriceDetails> stockPriceDetailsWriter(NamedParameterJdbcTemplate priceStoreJdbcTemplate){
        return new StockPriceDetailsJdbcWriter(priceStoreJdbcTemplate);
    }
}
