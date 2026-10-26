package com.settle;

public class LedgerRecord {
    public String tradeTime;
    public String orderId;
    public Integer quantity;
    public LedgerType type;

    public String getTradeTime() { return tradeTime; }
    public void setTradeTime(String v) { this.tradeTime = v; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String v) { this.orderId = v; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { this.quantity = v; }
    public LedgerType getType() { return type; }
    public void setType(LedgerType v) { this.type = v; }
}
