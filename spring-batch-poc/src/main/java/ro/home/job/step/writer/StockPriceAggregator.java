package ro.home.job.step.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemWriter;
import ro.home.job.model.FxMarketPricesStore;
import ro.home.job.model.StockPriceDetails;
import ro.home.job.model.Trade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class StockPriceAggregator implements ItemWriter<Trade> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceAggregator.class);

    private AtomicLong writerCounter = new AtomicLong(0);

    private FxMarketPricesStore fxMarketPricesStore;

    public StockPriceAggregator(FxMarketPricesStore fxMarketPricesStore) {
        this.fxMarketPricesStore = fxMarketPricesStore;
    }

    @Override
    public void write(List<? extends Trade> trades) throws Exception {
        trades.forEach(t -> {
            LOGGER.debug("Writing item number : {}", writerCounter.incrementAndGet());
            double price = t.getPrice();
            Date time = t.getTime();
            if (fxMarketPricesStore.containsKey(t.getStock())) {
                StockPriceDetails priceDetails = fxMarketPricesStore.get(t.getStock());
                if (price > priceDetails.getHighPrice()) {
                    priceDetails.setHighPrice(price);
                    priceDetails.setHighTime(time);
                }
                if (price < priceDetails.getLowPrice()) {
                    priceDetails.setLowPrice(price);
                    priceDetails.setLowTime(time);
                }
                // Set close price
                priceDetails.setClosePrice(price);
                priceDetails.setCloseTime(time);
                priceDetails.setTotalTradedShares(t.getShares());
                priceDetails.setTotalTradesCount(1);
                priceDetails.setTotalTradedValue(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(t.getShares())));
            } else {
                LOGGER.trace("Adding new stock {}", t.getStock());
                fxMarketPricesStore.put(t.getStock(), new StockPriceDetails(t.getStock(), price, time, price, time, price, time, price, time, t.getShares(),
                        1, BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(t.getShares()))
                ));
            }
        });
    }
}