package com.santossingh.onlineinventory.Models;

/**
 * Created by santoshsingh on 11/02/17.
 */

public class Orders {
    String buyer, mobile, address, product, qty, price, paid, balance, date, seller, key;
    private Orders values;

    public Orders() {
    }

    public Orders(String buyer, String mobile, String address, String product, String qty, String price, String paid, String balance, String date, String seller) {
        this.buyer = buyer;
        this.mobile = mobile;
        this.address = address;
        this.product = product;
        this.qty = qty;
        this.price = price;
        this.paid = paid;
        this.balance = balance;
        this.date = date;
        this.seller = seller;
    }

    public Orders getValues() {
        return values;
    }

    public void setValues(Orders values) {
        buyer = values.getBuyer();
        mobile = values.getMobile();
        address = values.getAddress();
        product = values.getProduct();
        qty = values.getQty();
        price = values.getPrice();
        paid = values.getPaid();
        balance = values.getBalance();
        date = values.getDate();
        seller = values.getSeller();
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
