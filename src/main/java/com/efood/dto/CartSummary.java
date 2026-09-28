package com.efood.dto;

public class CartSummary {

    private Double subtotal = 0.0;
    private Double deliveryFee = 0.0;
    private Double tax = 0.0;
    private Double total = 0.0;
    private int itemCount = 0;

    public CartSummary() {
    }

    public CartSummary(Double subtotal, Double deliveryFee, Double tax, Double total, int itemCount) {
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.tax = tax;
        this.total = total;
        this.itemCount = itemCount;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
