package ro.home.job.step.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import ro.home.job.model.FxMarketPricesStore;

import java.util.stream.Collectors;


public class PriceStoreTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceStoreTasklet.class);

    private final FxMarketPricesStore marketPricesStore;

    public PriceStoreTasklet(FxMarketPricesStore marketPricesStore) {
        this.marketPricesStore = marketPricesStore;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Market Store size : {}", marketPricesStore.size());
        LOGGER.debug("Stocks : [\n{}",
                String.join("\n", marketPricesStore.keySet().stream()
                        .map(stock -> marketPricesStore.get(stock).toString())
                        .collect(Collectors.toList()))
                        + "\n]");
        return RepeatStatus.FINISHED;
    }
}
