package ro.home.job.model;

import java.math.BigDecimal;
import java.util.Date;

import static java.lang.String.format;

public class StockPriceDetails {

    private String stock;
    private double openPrice;
    private Date openTime;
    private double closePrice;
    private Date closeTime;
    private double lowPrice;
    private Date lowTime;
    private double highPrice;
    private Date highTime;
    private int totalTradesCount;
    private long totalTradedShares;
    private BigDecimal totalTradedValue;

    public StockPriceDetails() {
        super();
    }

    public StockPriceDetails(String stock) {
        super();
        this.stock = stock;
    }


    public StockPriceDetails(String stock, double open, Date openTime, double close, Date closeTime, double low, Date lowTime,
                             double high, Date highTime, long tradedShares, int tradeCount) {
        super();
        this.stock = stock;
        this.openPrice = open;
        this.openTime = openTime;
        this.closePrice = close;
        this.closeTime = closeTime;
        this.lowPrice = low;
        this.lowTime = lowTime;
        this.highPrice = high;
        this.highTime = highTime;
        this.totalTradedShares = tradedShares;
        this.totalTradesCount = tradeCount;
        this.totalTradedValue = BigDecimal.ZERO;
    }

    public StockPriceDetails(String stock, double open, Date openTime, double close, Date closeTime, double low, Date lowTime,
                             double high, Date highTime, long tradedShares, int tradeCount, BigDecimal tradedValue) {
        super();
        this.stock = stock;
        this.openPrice = open;
        this.openTime = openTime;
        this.closePrice = close;
        this.closeTime = closeTime;
        this.lowPrice = low;
        this.lowTime = lowTime;
        this.highPrice = high;
        this.highTime = highTime;
        this.totalTradedShares = tradedShares;
        this.totalTradesCount = tradeCount;
        this.totalTradedValue = tradedValue;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public Date getLowTime() {
        return lowTime;
    }

    public void setLowTime(Date lowTime) {
        this.lowTime = lowTime;
    }

    public Date getHighTime() {
        return highTime;
    }

    public void setHighTime(Date highTime) {
        this.highTime = highTime;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public long getTotalTradedShares() {
        return totalTradedShares;
    }

    public void setTotalTradedShares(long tradedShares) {
        synchronized (this) {
            this.totalTradedShares = this.totalTradedShares + tradedShares;
        }
    }

    public int getTotalTradesCount() {
        return totalTradesCount;
    }

    public void setTotalTradesCount(int tradesCount) {
        synchronized (this) {
            this.totalTradesCount = totalTradesCount + tradesCount;
        }
    }

    public BigDecimal getTotalTradedValue() {
        return totalTradedValue;
    }

    public void setTotalTradedValue(BigDecimal tradedValue) {
        synchronized (this) {
            this.totalTradedValue = this.totalTradedValue.add(tradedValue);
        }
    }

    @Override
    public String toString() {
        return format("{stock= '%s', openPrice= %f, openTime= %s, closePrice= %f, closeTime= %s, lowPrice= %f, lowTime= %s, highPrice= %f, highTime= %s, totalTradesCount= %d, totalTradedShares= %d, totalTradedValue= %s}",
                stock, openPrice, openTime, closePrice, closeTime, lowPrice, lowTime, highPrice, highTime, totalTradesCount, totalTradedShares, totalTradedValue);
    }
}
