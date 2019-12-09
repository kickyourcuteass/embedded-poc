package ro.home.job.step.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;

import ro.home.job.model.StockPriceDetails;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StockPriceDetailsReader implements ItemStreamReader<StockPriceDetails> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceDetailsReader.class);

    private Set<Map.Entry<String, StockPriceDetails>> stockPriceDetailsEntries;
    private Iterator<Map.Entry<String, StockPriceDetails>> stockIterator;

    public StockPriceDetailsReader(Set<Map.Entry<String, StockPriceDetails>> stockPriceDetailsEntries) {
        this.stockPriceDetailsEntries = stockPriceDetailsEntries;
    }

    @Override
    public StockPriceDetails read() throws Exception {
        synchronized (this) {
            return stockIterator.hasNext() ? stockIterator.next().getValue() : null;
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        LOGGER.info("Setting iterator");
        LOGGER.info("Stock Price Repository size : {}", stockPriceDetailsEntries.size());
        stockIterator = stockPriceDetailsEntries.iterator();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
        LOGGER.info("closing reader");
    }
}
