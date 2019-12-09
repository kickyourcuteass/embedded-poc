package ro.home.job.step.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import ro.home.job.model.FxMarketEvent;
import ro.home.job.model.Trade;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class FxMarketEventProcessor implements ItemProcessor<FxMarketEvent, Trade> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FxMarketEventProcessor.class);

    private AtomicLong processorCounter = new AtomicLong(0);

    private final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.SS");

    @Override
    public Trade process(FxMarketEvent fxMarketEvent) throws Exception {
        LOGGER.debug("Processing item number : {}", processorCounter.incrementAndGet());
        String stock = fxMarketEvent.getStock();
        Date time = Date.from(LocalDateTime.now()
                .with(LocalTime.parse(fxMarketEvent.getTime()))
                .atZone(ZoneId.systemDefault()).toInstant());
        double price = Double.valueOf(fxMarketEvent.getPrice());
        long shares = Long.valueOf(fxMarketEvent.getShares());
        Trade trade = new Trade(stock, time, price, shares);
        LOGGER.trace("Converting (" + fxMarketEvent + ") into (" + trade + ")");
        return trade;
    }

}
