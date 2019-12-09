package ro.home.job.step.writer;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ro.home.job.model.StockPriceDetails;

import java.util.List;

import static java.util.Objects.isNull;


public class StockPriceDetailsJdbcWriter extends JdbcBatchItemWriter<StockPriceDetails> {

    public StockPriceDetailsJdbcWriter(NamedParameterJdbcTemplate priceStoreJdbcTemplate) {
        setJdbcTemplate(priceStoreJdbcTemplate);
        setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        setSql("INSERT INTO STOCK_PRICE_DETAILS (STOCK, OPEN_PRICE, OPEN_TIME, CLOSE_PRICE, CLOSE_TIME, LOW_PRICE, LOW_TIME, HIGH_PRICE, HIGH_TIME, TOTAL_TRADES_COUNT, TOTAL_TRADED_SHARES, TOTAL_TRADED_VALUE ) VALUES (:stock, :openPrice, :openTime, :closePrice, :closeTime, :lowPrice, :lowTime, :highPrice, :highTime, :totalTradesCount, :totalTradedShares, :totalTradedValue )");
    }

    @Override
    public void write(List<? extends StockPriceDetails> stockEntries) throws Exception {
        stockEntries.forEach(entry -> {
            if (isNull(entry)) {
                logger.warn("Attempting to write null stock entry");
            }
        });
        super.write(stockEntries);
    }
}
