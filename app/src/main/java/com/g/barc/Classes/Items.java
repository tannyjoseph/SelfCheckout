package com.g.barc.Classes;

public class Items {

    private String itemname;
    private String upc;
    private String price;

    public Items(String itemname, String upc, String sprice) {
        this.itemname = itemname;
        this.upc = upc;
        this.price = sprice;
    }

    public Items(){

    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
