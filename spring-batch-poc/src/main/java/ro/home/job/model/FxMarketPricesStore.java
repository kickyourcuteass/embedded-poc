package ro.home.job.model;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FxMarketPricesStore  {

    private Map<String, StockPriceDetails> stockPrices = new ConcurrentHashMap<>();
    //private Map<String, StockPriceDetails> stockPrices = Collections.synchronizedMap(new HashMap<>());

    public void clearPriceStore() {
        stockPrices.clear();
    }

    public Integer size() {
        return stockPrices.size();
    }

    public Set<Map.Entry<String, StockPriceDetails>> getStockPriceEntries() {
        return stockPrices.entrySet();
    }

    public boolean containsKey(String key) {
        return stockPrices.containsKey(key);
    }

    public StockPriceDetails put(String key, StockPriceDetails value) {
        return stockPrices.put(key, value);
    }

    public Collection<StockPriceDetails> values() {
        return stockPrices.values();
    }

    public StockPriceDetails get(String key) {
        return stockPrices.get(key);
    }

    public Set<String> keySet() {
        return new TreeSet<>(stockPrices.keySet());
    }
}
